package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.IOrigenRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import java.text.Normalizer;
import java.util.regex.Pattern;
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
  private final HechoConverter hechoConverter;
  private final WebClient webClient;

  public ProcesadorFuentesService(IHechoRepository hechoRepository, IOrigenRepository origenRepo,
      IFuenteRepository fuenteRepository, HechoConverter hechoConverter, WebClient.Builder webClientBuilder) {
    this.hechoRepository = hechoRepository;
    this.origenRepo = origenRepo;
    this.fuenteRepository = fuenteRepository;
    this.hechoConverter = hechoConverter;
    this.webClient = webClientBuilder.build();
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public CompletableFuture<Void> procesarFuenteAsync(String fuenteId, String coleccionId) {
    long start = System.currentTimeMillis();
    try {
      Fuente fuente = fuenteRepository.findById(fuenteId).orElseThrow();
      log.info("ASYNC: Inicio sincronización fuente: {}", fuente.getUrl());

      Set<Hecho> hechosEntrantes = fuente.obtenerHechosRefrescados(hechoConverter, webClient);
      if (hechosEntrantes.isEmpty()) {
        log.info("Fuente vacía o sin cambios. Fin.");
        return CompletableFuture.completedFuture(null);
      }

      Map<Long, Hecho> mapaHechosActualesEnDB = fuente.getHechos().stream()
          .filter(h -> h.getIdExterno() != null)
          .collect(Collectors.toMap(Hecho::getIdExterno, Function.identity(), (a, b) -> a));

      Map<String, Origen> mapaOrigenes = origenRepo.findAll().stream()
          .collect(Collectors.toMap(
              o -> o.getTipo() + "|" + (o.getAutor() != null ? o.getAutor().toLowerCase() : ""),
              Function.identity(),
              (a, b) -> a));

      Set<String> categoriasConocidas = hechoRepository.findAll().stream()
          .map(Hecho::getCategoria)
          .filter(Objects::nonNull)
          .map(this::normalizar)
          .collect(Collectors.toSet());

      Set<Hecho> listaParaGuardar = new HashSet<>();
      log.info("Procesando {} hechos en memoria...", hechosEntrantes.size());

      for (Hecho nuevo : hechosEntrantes) {
        Long idExt = nuevo.getIdExterno();

        if (idExt != null && mapaHechosActualesEnDB.containsKey(idExt)) {
          Hecho existente = mapaHechosActualesEnDB.get(idExt);

          if (sonDiferentes(existente, nuevo)) {
            existente.setTitulo(nuevo.getTitulo());
            existente.setDescripcion(nuevo.getDescripcion());
            existente.setCategoria(nuevo.getCategoria());
            existente.setFechaAcontecimiento(nuevo.getFechaAcontecimiento());

            if (ubicacionDiferente(existente, nuevo)) {
              existente.setUbicacion(nuevo.getUbicacion());
            }
            actualizarMultimedia(existente, nuevo);
          }
          listaParaGuardar.add(existente);
          mapaHechosActualesEnDB.remove(idExt);
        } else {
          String keyOrigen = nuevo.getOrigen().getTipo() + "|" +
              (nuevo.getOrigen().getAutor() != null ? nuevo.getOrigen().getAutor().toLowerCase() : "");

          if (mapaOrigenes.containsKey(keyOrigen)) {
            nuevo.setOrigen(mapaOrigenes.get(keyOrigen));
          }
          if (nuevo.getCategoria() != null) {
            String nuevaCategoriaNorm = normalizar(nuevo.getCategoria());
            String categoriaCorregida = buscarCategoriaMasParecida(nuevaCategoriaNorm, categoriasConocidas);
            nuevo.setCategoria(categoriaCorregida);
          }
          listaParaGuardar.add(nuevo);
        }
      }

      if (!mapaHechosActualesEnDB.isEmpty()) {
        log.info("Detectados {} hechos eliminados en la fuente.", mapaHechosActualesEnDB.size());
        for (Hecho hBorrar : mapaHechosActualesEnDB.values()) {
          fuente.getHechos().remove(hBorrar);
        }
      }

      log.info("Guardando {} hechos...", listaParaGuardar.size());
      List<Hecho> hechosGuardados = hechoRepository.saveAll(listaParaGuardar);
      fuente.setHechos(new HashSet<>(hechosGuardados));
      fuenteRepository.save(fuente);
      long duration = System.currentTimeMillis() - start;
      log.info("ASYNC: Fin exitoso fuente {}. Tiempo total: {}ms", fuenteId, duration);
      return CompletableFuture.completedFuture(null);

    } catch (Exception e) {
      log.error("ASYNC ERROR: Falló procesamiento de fuente {}", fuenteId, e);
      return CompletableFuture.completedFuture(null);
    }
  }

  private boolean sonDiferentes(Hecho existente, Hecho nuevo) {
    if (!Objects.equals(existente.getTitulo(), nuevo.getTitulo()) ||
        !Objects.equals(existente.getDescripcion(), nuevo.getDescripcion()) ||
        !Objects.equals(existente.getCategoria(), nuevo.getCategoria())) {
      return true;
    }

    if (ubicacionDiferente(existente, nuevo))
      return true;

    if (nuevo.getMultimedia() != null && !nuevo.getMultimedia().isEmpty())
      return true;

    if (existente.getFechaAcontecimiento() == null && nuevo.getFechaAcontecimiento() == null)
      return false;
    if (existente.getFechaAcontecimiento() == null || nuevo.getFechaAcontecimiento() == null)
      return true;

    return !existente.getFechaAcontecimiento().toLocalDate()
        .isEqual(nuevo.getFechaAcontecimiento().toLocalDate());
  }

  private boolean ubicacionDiferente(Hecho existente, Hecho nuevo) {
    if (existente.getUbicacion() == null && nuevo.getUbicacion() == null)
      return false;
    if (existente.getUbicacion() == null || nuevo.getUbicacion() == null)
      return true;
    return !Objects.equals(existente.getUbicacion().getLatitud(), nuevo.getUbicacion().getLatitud()) ||
        !Objects.equals(existente.getUbicacion().getLongitud(), nuevo.getUbicacion().getLongitud());
  }

  private void actualizarMultimedia(Hecho existente, Hecho nuevo) {
    existente.getMultimedia().clear();
    if (nuevo.getMultimedia() != null) {
      nuevo.getMultimedia().forEach(m -> {
        m.setHecho(existente);
        existente.getMultimedia().add(m);
      });
    }
  }

  private String normalizar(String input) {
    if (input == null)
      return "";
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(normalized).replaceAll("").toLowerCase().trim();
  }

  private String buscarCategoriaMasParecida(String input, Set<String> categoriasConocidas) {
    if (input == null)
      return "Sin Categoría";
    String inputNorm = normalizar(input);
    if (categoriasConocidas.contains(inputNorm)) {
      return input;
    }

    String mejorCoincidencia = input;
    LevenshteinDistance levenshtein = new LevenshteinDistance(3);
    int menorDistancia = Integer.MAX_VALUE;

    for (String categoriaOficial : categoriasConocidas) {
      String oficialNorm = normalizar(categoriaOficial);
      if (oficialNorm.length() > 4 && inputNorm.contains(oficialNorm)) {
        return categoriaOficial;
      }

      Integer distancia = levenshtein.apply(inputNorm, oficialNorm);

      if (distancia != -1 && distancia < menorDistancia) {
        menorDistancia = distancia;
        mejorCoincidencia = categoriaOficial;
      }
    }

    if (menorDistancia <= 3) {
      return mejorCoincidencia;
    } else {
      return input;
    }
  }
}