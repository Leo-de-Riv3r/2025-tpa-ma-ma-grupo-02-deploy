package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Coleccion; // Importar Coleccion
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion; // Importar Enum
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository; // Importar Repo
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.IOrigenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ProcesadorFuentesService {

  private final IHechoRepository hechoRepository;
  private final IOrigenRepository origenRepo;
  private final IFuenteRepository fuenteRepository;
  private final IColeccionRepository coleccionRepository; // Necesario para actualizar estado
  private final HechoConverter hechoConverter;
  private final WebClient webClient;

  public ProcesadorFuentesService(IHechoRepository hechoRepository, IOrigenRepository origenRepo,
                                  IFuenteRepository fuenteRepository, IColeccionRepository coleccionRepository,
                                  HechoConverter hechoConverter, WebClient.Builder webClientBuilder) {
    this.hechoRepository = hechoRepository;
    this.origenRepo = origenRepo;
    this.fuenteRepository = fuenteRepository;
    this.coleccionRepository = coleccionRepository;
    this.hechoConverter = hechoConverter;
    this.webClient = webClientBuilder.build();
  }

  private Boolean hechoLocalNoActualizado(Hecho h, Set<Hecho> hechos) {
    return hechos.stream().filter(hechoRefrescado -> hechoRefrescado.getTitulo().equals(h.getTitulo()) && hechoRefrescado.getDescripcion().equals(h.getDescripcion()) && hechoRefrescado.getCategoria().equals(h.getCategoria()) ).findFirst().isEmpty();
  }
  @Async
  @Transactional
  public void procesarFuenteAsync(String fuenteId, String coleccionId) {
    try {
      Fuente fuente = fuenteRepository.findById(fuenteId).orElseThrow();
      log.info("ASYNC: Iniciando descarga para fuente: {}", fuente.getUrl());

      Set<Hecho> hechos = fuente.obtenerHechosRefrescados(hechoConverter, webClient);

      Map<Hecho, String> clavesNuevas = hechos.stream()
          .collect(Collectors.toMap(
              Function.identity(),
              h -> h.getTitulo() + "|" + h.getDescripcion() + "|" + h.getFechaAcontecimiento()
          ));

      Set<String> clavesSet = new HashSet<>(clavesNuevas.values());

      List<Hecho> hechosExistentes = hechoRepository.findAll();

      Map<String, Hecho> hechosExistentesMap = new HashMap<>(hechosExistentes.size());

      hechosExistentes.forEach(h ->
          hechosExistentesMap.put(
              h.getTitulo() + "|" + h.getDescripcion() + "|" + h.getFechaAcontecimiento(),
              h
          )
      );

      // 4) traer orígenes una vez
      List<Origen> origenes = origenRepo.findAll();
      Map<String, Origen> origenesMap = new HashMap<>(origenes.size());

      // 5) lista para inserts batch
      List<Hecho> hechosParaGuardar = new ArrayList<>();


      if(fuente.getTipoFuente() == TipoFuente.DINAMICA) {
        Set<Hecho> hechosFuente = fuente.getHechos();
        hechosFuente.forEach(h -> {
          if (hechoLocalNoActualizado(h, hechos)) {
            fuente.removeHecho(h);
          }
        });
      }

      if (hechos.isEmpty()) {
        log.warn("ASYNC: Fuente devolvió 0 hechos.");
      } else {
        for (Hecho h : hechos) {
          String key = clavesNuevas.get(h);
          if (hechosExistentesMap.containsKey(key)) {
            // ya existe → reutilizar
            fuente.addHecho(hechosExistentesMap.get(key));

            continue;
          }

          // normalizar origen
          String origenKey = h.getOrigen().getTipo() + "|" + h.getOrigen().getAutor();
          Origen origenNormalizado = origenesMap.get(origenKey);
          if (origenNormalizado != null) {
            h.setOrigen(origenNormalizado);
          } else {
            Origen nuevoOrigen =origenRepo.save(h.getOrigen());
            origenesMap.put(origenKey, nuevoOrigen);
            h.setOrigen(nuevoOrigen);
          }

          hechoRepository.buscarCategoriaNormalizada(h.getCategoria())
              .ifPresent(h::setCategoria);

          hechosParaGuardar.add(h);
        }

        if (!hechosParaGuardar.isEmpty()) {
          List<Hecho> guardados = hechoRepository.saveAll(hechosParaGuardar);
          guardados.forEach(fuente::addHecho);
        }

      }

        log.info("ASYNC: Procesamiento finalizado para fuente {}. Hechos guardados: {}", fuente.getUrl(),
            hechos.size());
      if (coleccionId != null) {
        coleccionRepository.findById(coleccionId).ifPresent(c -> {
          c.setEstado(EstadoColeccion.DISPONIBLE);
          c.actualizarHechosFiltrados();
          c.refrescarHechosCurados();
          coleccionRepository.save(c);
          log.info("ASYNC: Colección {} marcada como DISPONIBLE", c.getId());
        });
      }

    } catch (Exception e) {
      log.error("ASYNC ERROR: Falló procesamiento de fuente {}", fuenteId, e);
    }
  }
}
