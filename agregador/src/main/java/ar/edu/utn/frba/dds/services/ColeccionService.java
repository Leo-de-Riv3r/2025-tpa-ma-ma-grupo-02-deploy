package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ColeccionService {
  private final IColeccionRepository coleccionRepository;
  private final FuenteService fuenteService;
  private final SolicitudService solicitudService;
  private final IHechoRepository hechoRepository;

  @PersistenceContext
  private EntityManager em;

  public ColeccionService(IColeccionRepository coleccionRepository, FuenteService fuenteService, SolicitudService solicitudService, IHechoRepository hechoRepository) {
    this.coleccionRepository = coleccionRepository;
    this.fuenteService = fuenteService;
    this.solicitudService = solicitudService;
    this.hechoRepository = hechoRepository;
  }

  public Coleccion createColeccion(ColeccionDTOEntrada dto) {
    Coleccion coleccion = new Coleccion();
    coleccion.setTitulo(dto.getTitulo());
    coleccion.setDescripcion(dto.getDescripcion());

    if (dto.getAlgoritmo() != null) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmo().toUpperCase());
        IConsensoStrategy algoritmoConsenso = tipoAlgoritmo.getStrategy();
        coleccion.setAlgoritmoConsenso(algoritmoConsenso);
      } catch (Exception e){
        throw new IllegalArgumentException("Algoritmo de tipo " + dto.getAlgoritmo() + " no aceptado");
      }
    }
    if (dto.getFuentes() != null) {
      Set<Fuente> fuentes = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = fuenteService.getFuente(fuenteDTO.getId());
        fuentes.add(fuente);
      });
      coleccion.setFuentes(fuentes);
    }

    if(dto.getFiltros() != null) {
      Set<IFiltroStrategy> filtros = dto.getFiltros().stream().map(dtoFiltro -> FiltroStrategyFactory.fromDTO(dtoFiltro)).collect(Collectors.toSet());
      filtros.forEach(f -> coleccion.addCriterio(f));
    }
    return coleccionRepository.save(coleccion);
  }

  public List<ColeccionDTOSalida> getColeccionesDTO() {
    List<Coleccion> colecciones = coleccionRepository.findAll();
    return colecciones.stream().map(c -> convertirColeccionADTO(c)).toList();
  }

  public List<Coleccion> getColecciones() {
    return coleccionRepository.findAll();
  }

  public ColeccionDTOSalida getColeccionDTO(String coleccionId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    return this.convertirColeccionADTO(coleccion);
  }

  private ColeccionDTOSalida convertirColeccionADTO(Coleccion coleccion) {
    ColeccionDTOSalida respuesta = new ColeccionDTOSalida();
    respuesta.setId(coleccion.getId());
    respuesta.setTitulo(coleccion.getTitulo());
    respuesta.setDescripcion(coleccion.getDescripcion());
    Set<Fuente> fuentes = coleccion.getFuentes();
    respuesta.setFuentes(fuentes.stream().map(f -> new FuenteDTOOutput(f.getId(), f.getTipoFuente(), f.getUrl())).toList());
    respuesta.setCantSolicitudesSpam(this.obtenerCantSolicitudesSpam(coleccion.getHechos()));
    return respuesta;
  }
  private Integer obtenerCantSolicitudesSpam(Set<Hecho> hechos) {
    AtomicReference<Integer> cantidadSolicitudesSpam = new AtomicReference<>(0);
    hechos .forEach(h -> {
      cantidadSolicitudesSpam.getAndSet(cantidadSolicitudesSpam.get() + solicitudService.cantidadSolicitudesSpam(h.getId()));
    });
    return cantidadSolicitudesSpam.get();
  }

  public Coleccion getColeccion(String coleccionId) {
    return coleccionRepository
        .findById(coleccionId)
        .orElseThrow(() -> new EntityNotFoundException("Coleccion con id " + coleccionId + " no encontrada"));
  }

  @Transactional
  public void updateColeccion(String coleccionId, ColeccionDTOEntrada dto) {
    Coleccion coleccion = this.getColeccion(coleccionId);

    if (dto.getTitulo() != null) {
      coleccion.setTitulo(dto.getTitulo());
    }
    if (dto.getDescripcion() != null) {
      coleccion.setDescripcion(dto.getDescripcion());
    }
    if (dto.getFuentes() != null) {
      Set<Fuente> fuentes = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = fuenteService.getFuente(fuenteDTO.getId());
        fuentes.add(fuente);
      });
      coleccion.limpiarFuentes();
      coleccion.setearFuentes(fuentes);
    }
    if (dto.getAlgoritmo() != null) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmo().toUpperCase());
        IConsensoStrategy algoritmoConsenso = tipoAlgoritmo.getStrategy();
        coleccion.setAlgoritmoConsenso(algoritmoConsenso);
      } catch (Exception e){
        throw new IllegalArgumentException("Algoritmo de tipo " + dto.getAlgoritmo() + " no aceptado");
      }
    }
    coleccionRepository.save(coleccion);
  }

  public void deleteColeccion(String coleccionId) {
    coleccionRepository.deleteById(coleccionId);
  }

//  public void refrescoColecciones() {
//    List <Coleccion> colecciones = this.getColecciones();
//    colecciones.forEach(coleccion -> coleccion.getFuentes().forEach(fuente -> refrescoColecciones()));
//  }

  public Set<Hecho> getHechos(String coleccionId, boolean navegacionCurada, Integer page, Integer perPage, Set<IFiltroStrategy> filtros) {
    Set<Hecho> hechos = Set.of();
    if (coleccionId != null) {
      Optional<Coleccion> coleccion = coleccionRepository.findById(coleccionId);
      if (navegacionCurada) {
        hechos = coleccion.map(Coleccion::getHechosCurados).orElse(null);
      } else {
        hechos = coleccion.map(Coleccion::getHechos).orElse(null);
      }
    }

    if (hechos == null || hechos.isEmpty()) {
      List<Coleccion> colecciones = this.getColecciones();
      for (Coleccion coleccion : colecciones) {
        Set<Hecho> hechosDeColeccion = navegacionCurada
            ? coleccion.getHechosCurados()
            : coleccion.getHechos();
        if (hechosDeColeccion != null && !hechosDeColeccion.isEmpty()) {
          if (hechos != null) {
            hechos.addAll(hechosDeColeccion);
          }
        }
      }
    }

    if (page != null && perPage != null) {
      return hechos != null ? hechos.stream()
          .skip((long) (page - 1) * perPage)
          .limit(perPage)
          .collect(HashSet::new, HashSet::add, HashSet::addAll) : null;
    }

    if (filtros != null) {
      return hechos != null ? hechos.stream()
          .filter(h -> h.cumpleFiltros(filtros))
          .collect(Collectors.toSet()) : null;
    }

    return hechos.stream().filter(h -> hechoRepetido(h)).collect(Collectors.toSet());
  }

  public Boolean hechoRepetido(Hecho h) {
  return hechoRepository.existsByTituloAndDescripcionAndCategoria(h.getTitulo(), h.getDescripcion(), h.getCategoria());
  }

  public void addFuente(String coleccionId, FuenteDTO dto) {
    Fuente fuente = fuenteService.getFuente(dto.getId());
    Coleccion coleccion = this.getColeccion(coleccionId);
    coleccion.addFuente(fuente);
    coleccionRepository.save(coleccion);
  }

  public void removeFuente(String coleccionId, String fuenteId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    coleccion.removeFuente(fuenteId);
    coleccionRepository.save(coleccion);
  }

  public void updateAlgoritmoConsenso(String coleccionId, CambioAlgoritmoDTO algoritmoDTO) {
    ColeccionDTOEntrada dto = new ColeccionDTOEntrada();
    dto.setAlgoritmo(algoritmoDTO.getTipoAlgoritmo());
    updateColeccion(coleccionId, dto);
  }

  //OK
  @Transactional
  public void refrescarHechosCurados() {
    List <Coleccion> colecciones = coleccionRepository.findAll();
//    em.createQuery(
//            "DELETE FROM hecho_consensuado hc WHERE hc.hecho_id IN " +
//            "(SELECT id from hecho h WHERE h.fuente_id IS NULL)"
//        )
//        .executeUpdate();
    colecciones.forEach(coleccion ->  coleccion.refrescarHechosCurados(em));
    //elimino los hechos con fuente_id nulo porque ya son obsoletos

    coleccionRepository.saveAll(colecciones);
  }

  public void addCriterio(String id, IFiltroStrategy filtro) {
    Coleccion coleccion = this.getColeccion(id);
    coleccion.addCriterio(filtro);
    coleccionRepository.save(coleccion);
  }
}
