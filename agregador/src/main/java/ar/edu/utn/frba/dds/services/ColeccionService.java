package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.ColeccionGQLDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.ResumenActividadDTOSalida;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.*;
import ar.edu.utn.frba.dds.models.entities.utils.ColeccionConverter;
import ar.edu.utn.frba.dds.models.entities.utils.FuenteConverter;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import ar.edu.utn.frba.dds.models.events.FuentesAProcesarEvent;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.IOrigenRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudEliminacionRepository;
import ar.edu.utn.frba.dds.models.repositories.specs.FiltroSpecFactory;
import ar.edu.utn.frba.dds.models.repositories.specs.HechoSpecs;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ColeccionService {
  private final IColeccionRepository coleccionRepository;
  private final SolicitudEliminacionService solicitudService;
  private final IHechoRepository hechoRepository;
  private final FuenteConverter fuenteConverter;
  private final ColeccionConverter coleccionConverter;
  private final HechoConverter hechoConverter;
  private final IFuenteRepository fuenteRepository;
  private final ISolicitudEliminacionRepository solicitudRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final FiltroSpecFactory filtroSpecFactory;

  public ColeccionService(
      IColeccionRepository coleccionRepository,
      SolicitudEliminacionService solicitudService,
      IHechoRepository hechoRepository,
      IOrigenRepository origenRepo,
      FuenteConverter fuenteConverter,
      ColeccionConverter coleccionConverter,
      HechoConverter hechoConverter,
      IFuenteRepository fuenteRepository,
      ISolicitudEliminacionRepository solicitudRepository,
      ApplicationEventPublisher eventPublisher,
      FiltroSpecFactory filtroSpecFactory) {
    this.coleccionRepository = coleccionRepository;
    this.solicitudService = solicitudService;
    this.hechoRepository = hechoRepository;
    this.fuenteConverter = fuenteConverter;
    this.coleccionConverter = coleccionConverter;
    this.hechoConverter = hechoConverter;
    this.fuenteRepository = fuenteRepository;
    this.solicitudRepository = solicitudRepository;
    this.eventPublisher = eventPublisher;
    this.filtroSpecFactory = filtroSpecFactory;
  }

  @Transactional
  public ColeccionDTOSalida createColeccion(ColeccionDTOEntrada dto) {
    Coleccion coleccion = new Coleccion();
    coleccion.setTitulo(dto.getTitulo());
    coleccion.setDescripcion(dto.getDescripcion());

    Boolean calcularConsenso = false;
    if (dto.getAlgoritmoConsenso() != null && !dto.getAlgoritmoConsenso().isEmpty()) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmoConsenso().toUpperCase());
        coleccion.setAlgoritmoConsenso(tipoAlgoritmo.getStrategy());
        calcularConsenso = true;
      } catch (Exception e) {
        throw new IllegalArgumentException("Algoritmo de tipo " + dto.getAlgoritmoConsenso() + " no aceptado");
      }
    }

    List<String> idsFuentes = new ArrayList<>();
    if (dto.getFuentes() != null) {
      Set<Fuente> fuentes = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = fuenteConverter.fromDTO(fuenteDTO);
        Optional<Fuente> fuenteExistente = fuenteRepository.findByUrlAndTipoFuente(
            fuente.getUrl(), fuente.getTipoFuente());
        if (fuenteExistente.isPresent()) {
          fuente = fuenteExistente.get();
        } else {
          idsFuentes.add(fuente.getId());
          fuente = fuenteRepository.save(fuente);
        }
        fuentes.add(fuente);
      });
      coleccion.setearFuentes(fuentes);
    }
    if (dto.getCriterios() != null) {
      Set<IFiltroStrategy> criterios = dto.getCriterios().stream()
          .map(FiltroStrategyFactory::fromDTO).collect(Collectors.toSet());
      coleccion.setearCriterios(criterios);
    }
    if (!idsFuentes.isEmpty() || calcularConsenso) {
      coleccion.setEstado(EstadoColeccion.PROCESANDO);
    }
    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);

    if (!idsFuentes.isEmpty()) {
      eventPublisher.publishEvent(new FuentesAProcesarEvent(
          coleccionGuardada.getId(), idsFuentes, calcularConsenso));
    }
    return coleccionConverter.fromEntity(coleccionGuardada);
  }

  @Transactional
  public void updateColeccion(String coleccionId, ColeccionDTOEntrada dto) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    boolean recalcularConsenso = false;

    if (dto.getTitulo() != null)
      coleccion.setTitulo(dto.getTitulo());
    if (dto.getDescripcion() != null)
      coleccion.setDescripcion(dto.getDescripcion());

    if (dto.getFuentes() != null) {
      Set<String> idsFuentesActuales = coleccion.getFuentes().stream().map(Fuente::getId).collect(Collectors.toSet());
      Set<Fuente> fuentesNuevas = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = fuenteConverter.fromDTO(fuenteDTO);
        Optional<Fuente> fuenteExistente = fuenteRepository.findByUrlAndTipoFuente(
            fuente.getUrl(), fuente.getTipoFuente());
        if (fuenteExistente.isPresent()) {
          fuente = fuenteExistente.get();
        } else {
          fuente = fuenteRepository.save(fuente);
        }
        fuentesNuevas.add(fuente);
      });
      Set<String> idsFuentesNuevas = fuentesNuevas.stream().map(Fuente::getId).collect(Collectors.toSet());

      if (!idsFuentesActuales.equals(idsFuentesNuevas))
        recalcularConsenso = true;

      coleccion.setearFuentes(fuentesNuevas);
    }

    if (dto.getAlgoritmoConsenso() != null && !dto.getAlgoritmoConsenso().isEmpty()) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmoConsenso().toUpperCase());
        if (coleccion.getAlgoritmoConsenso() == null
            || !coleccion.getAlgoritmoConsenso().getTipo().equals(tipoAlgoritmo)) {
          recalcularConsenso = true;
        }
        coleccion.setAlgoritmoConsenso(tipoAlgoritmo.getStrategy());
      } catch (Exception e) {
        throw new IllegalArgumentException("Algoritmo inválido");
      }
    } else if (dto.getAlgoritmoConsenso() != null && dto.getAlgoritmoConsenso().isEmpty()) {
      recalcularConsenso = true;
      coleccion.setAlgoritmoConsenso(null);
    }

    if (dto.getCriterios() != null) {
      coleccion
          .setearCriterios(dto.getCriterios().stream().map(FiltroStrategyFactory::fromDTO).collect(Collectors.toSet()));
      recalcularConsenso = true;
    } else {
      if (!coleccion.getCriterios().isEmpty()) {
        coleccion.clearCriterios();
        recalcularConsenso = true;
      }
    }

    if (recalcularConsenso) {
      coleccion.setEstado(EstadoColeccion.PROCESANDO);
      coleccionRepository.save(coleccion);
      eventPublisher.publishEvent(new FuentesAProcesarEvent(coleccion.getId(), new ArrayList<>(), true));
    } else {
      coleccionRepository.save(coleccion);
    }
  }

  @Transactional
  public void refrescoFuentes() {
    List<Fuente> fuentes = fuenteRepository.findAll();
    if (fuentes.isEmpty())
      return;
    List<String> todosLosIds = fuentes.stream().map(Fuente::getId).toList();

    List<Coleccion> afectadas = coleccionRepository.findColeccionesByFuentesId(todosLosIds);
    for (Coleccion c : afectadas)
      c.setEstado(EstadoColeccion.PROCESANDO);
    coleccionRepository.saveAll(afectadas);

    eventPublisher.publishEvent(new FuentesAProcesarEvent(null, todosLosIds, true));
    log.info("Se disparó el refresco masivo para {} fuentes.", todosLosIds.size());
  }

  @Transactional
  public void refrescarFuenteDinamica() {
    Optional<Fuente> fuenteDinamica = fuenteRepository.findByTipoFuente(TipoFuente.DINAMICA);
    if (fuenteDinamica.isPresent()) {
      String fuenteId = fuenteDinamica.get().getId();
      List<Coleccion> afectadas = coleccionRepository.findColeccionesByFuentesId(List.of(fuenteId));
      for (Coleccion c : afectadas)
        c.setEstado(EstadoColeccion.PROCESANDO);
      coleccionRepository.saveAll(afectadas);

      eventPublisher.publishEvent(new FuentesAProcesarEvent(null, List.of(fuenteId), true));
      log.info("Se solicitó refresco manual para fuente dinamica {}", fuenteId);
    }
  }

  @Transactional
  public void refrescarColeccionesAfectadas(List<String> fuenteIds) {
    if (fuenteIds == null || fuenteIds.isEmpty())
      return;
    log.info("Buscando colecciones afectadas por actualización de fuentes: {}", fuenteIds);
    List<Coleccion> coleccionesAfectadas = coleccionRepository.findColeccionesByFuentesId(fuenteIds);
    for (Coleccion coleccion : coleccionesAfectadas) {
      try {
        log.info("Refrescando consenso automáticamente para colección: {}", coleccion.getId());
        this.optimizarYRefrescar(coleccion);
        coleccion.setEstado(EstadoColeccion.DISPONIBLE);
        coleccionRepository.save(coleccion);
      } catch (Exception e) {
        log.error("Error al refrescar colección {}: {}", coleccion.getId(), e.getMessage());
        coleccion.setEstado(EstadoColeccion.DISPONIBLE);
        coleccionRepository.save(coleccion);
      }
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void refrescarHechosCurados(String coleccionId) {
    if (coleccionId != null) {
      log.info("Iniciando cálculo de consenso para colección: {}", coleccionId);
      Coleccion coleccion = coleccionRepository.findById(coleccionId).orElseThrow();
      optimizarYRefrescar(coleccion);
      coleccionRepository.save(coleccion);
      log.info("Fin cálculo de consenso para colección: {}", coleccionId);
    }
  }

  private void optimizarYRefrescar(Coleccion coleccion) {
    if (coleccion.getAlgoritmoConsenso() == null) {
      return;
    }
    List<String> fuenteIds = coleccion.getFuentes().stream().map(Fuente::getId).toList();
    if (fuenteIds.isEmpty()) {
      coleccion.getAlgoritmoConsenso().getHechosConsensuados().clear();
      return;
    }

    IConsensoStrategy estrategia = coleccion.getAlgoritmoConsenso();
    Integer totalFuentes = fuenteIds.size();
    long minRequerido = estrategia.calcularMinimoRequerido(totalFuentes);
    estrategia.setCantidadMinimaApariciones((int) minRequerido);

    List<Hecho> hechosCrudos;
    if (estrategia.getTipo() == TipoAlgoritmo.MULTIPLES_MENCIONES) {
      log.info("Aplicando consenso estricto (Múltiples Menciones) para colección {}", coleccion.getId());
      hechosCrudos = hechoRepository.findHechosConsensuadosEstricto(fuenteIds, minRequerido);
    } else {
      hechosCrudos = hechoRepository.findHechosConsensuados(fuenteIds, minRequerido);
    }

    final List<Hecho> nuevosHechosConsensuados;
    if (coleccion.getCriterios() != null && !coleccion.getCriterios().isEmpty()) {
      nuevosHechosConsensuados = hechosCrudos.stream()
          .filter(h -> h.cumpleFiltros(coleccion.getCriterios()))
          .collect(Collectors.toList());
    } else {
      nuevosHechosConsensuados = hechosCrudos;
    }

    Set<Hecho> actuales = estrategia.getHechosConsensuados();
    List<Hecho> aEliminar = actuales.stream().filter(h -> !nuevosHechosConsensuados.contains(h)).toList();
    List<Hecho> aAgregar = nuevosHechosConsensuados.stream().filter(h -> !actuales.contains(h)).toList();

    if (!aEliminar.isEmpty())
      actuales.removeAll(aEliminar);
    if (!aAgregar.isEmpty())
      actuales.addAll(aAgregar);

    log.info("Colección {}: Fuentes={}, Min={}, Cambios: +{} / -{}", coleccion.getId(), totalFuentes, minRequerido,
        aAgregar.size(), aEliminar.size());
  }

  @Transactional(readOnly = true)
  public PaginacionDTOSalida<HechoDTOSalida> getHechos(String coleccionId, boolean navegacionCurada, Integer page,
      Set<IFiltroStrategy> filtrosUsuario) {

    int pageSize = 500;
    int pageNumber = (page == null || page < 1) ? 0 : page - 1;
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("fechaAcontecimiento").descending());

    Specification<Hecho> spec = Specification.where(HechoSpecs.excluirEliminados());

    if (coleccionId != null) {
      Coleccion coleccion = coleccionRepository.findById(coleccionId)
          .orElseThrow(() -> new EntityNotFoundException("Coleccion no encontrada"));

      // Aplicar criterios internos de la colección (Filtros persistidos)
      if (coleccion.getCriterios() != null && !coleccion.getCriterios().isEmpty()) {
        for (IFiltroStrategy criterioInterno : coleccion.getCriterios()) {
          spec = spec.and(filtroSpecFactory.getSpec(criterioInterno));
        }
      }

      if (navegacionCurada && coleccion.getAlgoritmoConsenso() != null) {
        spec = spec.and(HechoSpecs.deConsenso(coleccion.getAlgoritmoConsenso().getId()));
      } else {
        List<String> fuenteIds = coleccion.getFuentes().stream().map(Fuente::getId).toList();
        if (fuenteIds.isEmpty()) {
          return new PaginacionDTOSalida<>(new ArrayList<>(), 1, 0);
        }

        spec = spec.and(HechoSpecs.deFuentes(fuenteIds));
      }
    }

    if (filtrosUsuario != null && !filtrosUsuario.isEmpty()) {
      for (IFiltroStrategy filtroExterno : filtrosUsuario)
        spec = spec.and(filtroSpecFactory.getSpec(filtroExterno));
    }

    Page<Hecho> pageResult = hechoRepository.findAll(spec, pageable);
    List<HechoDTOSalida> dtos = pageResult.getContent().stream().map(hechoConverter::fromEntity)
        .collect(Collectors.toList());

    return new PaginacionDTOSalida<>(dtos, pageResult.getNumber() + 1, pageResult.getTotalPages());
  }

  @Transactional
  public void deleteColeccion(String coleccionId) {
    coleccionRepository.deleteById(coleccionId);
    log.info("EVENTO_ELIMINACION - Colección eliminada. ID: {}", coleccionId);
  }

  @Transactional(readOnly = true)
  public List<ColeccionDTOSalida> getColeccionesDTO() {
    List<Coleccion> colecciones = coleccionRepository.findAll();
    return colecciones.stream().map(coleccionConverter::fromEntity).toList();
  }

  @Transactional(readOnly = true)
  public ColeccionDTOSalida getColeccionDTO(String coleccionId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    return coleccionConverter.fromEntity(coleccion);
  }

  @Transactional
  public void addFuente(String coleccionId, FuenteDTO dto) {
    Fuente fuente = fuenteConverter.fromDTO(dto);
    Coleccion coleccion = this.getColeccion(coleccionId);
    coleccion.addFuente(fuente);
    coleccion.setEstado(EstadoColeccion.PROCESANDO);
    coleccionRepository.save(coleccion);
    eventPublisher.publishEvent(new FuentesAProcesarEvent(coleccion.getId(), List.of(fuente.getId()), true));
  }

  @Transactional
  public void removeFuente(String coleccionId, String fuenteId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    coleccion.removeFuente(fuenteId);
    coleccion.setEstado(EstadoColeccion.PROCESANDO);
    coleccionRepository.save(coleccion);
    eventPublisher.publishEvent(new FuentesAProcesarEvent(coleccion.getId(), new ArrayList<>(), true));
  }

  @Transactional
  public void updateAlgoritmoConsenso(String coleccionId, CambioAlgoritmoDTO algoritmoDTO) {
    ColeccionDTOEntrada dto = new ColeccionDTOEntrada();
    dto.setAlgoritmoConsenso(algoritmoDTO.getTipoAlgoritmo());
    updateColeccion(coleccionId, dto);
  }

  @Transactional
  public void addCriterio(String id, IFiltroStrategy filtro) {
    Coleccion coleccion = this.getColeccion(id);
    coleccion.addCriterio(filtro);
    coleccion.setEstado(EstadoColeccion.PROCESANDO);
    coleccionRepository.save(coleccion);
    eventPublisher.publishEvent(new FuentesAProcesarEvent(coleccion.getId(), new ArrayList<>(), true));
  }

  @Transactional(readOnly = true)
  public HechoDetallesDTOSalida getHechoDTO(Long idHecho) {
    Hecho hecho = hechoRepository.findById(idHecho)
        .orElseThrow(() -> new EntityNotFoundException("Hecho no encontrado"));
    return hechoConverter.fromEntityDetails(hecho);
  }

  @Transactional(readOnly = true)
  public ResumenActividadDTOSalida getResumenActividad() {
    ResumenActividadDTOSalida resumen = new ResumenActividadDTOSalida();
    resumen.setHechostotales(hechoRepository.count());
    resumen.setFuentesTotales(fuenteRepository.count());
    resumen.setSolicitudesEliminacion(solicitudRepository.countByEstadoActual_Estado(TipoEstado.PENDIENTE));
    return resumen;
  }

  @Transactional(readOnly = true)
  public ColeccionGQLDTOSalida getColeccionOutputDTO(String id, Boolean curadosFinal, Integer page,
      Set<IFiltroStrategy> filtros) {
    Coleccion coleccion = this.getColeccion(id);
    ColeccionDTOSalida coleccionDTO = coleccionConverter.fromEntity(coleccion);
    ColeccionGQLDTOSalida respuesta = new ColeccionGQLDTOSalida(coleccionDTO);
    respuesta.setHechos(this.getHechos(id, curadosFinal, page, filtros));
    return respuesta;
  }

  @Transactional(readOnly = true)
  public List<String> obtenerTodosLosIdsColecciones() {
    return coleccionRepository.findAll().stream().map(Coleccion::getId).collect(Collectors.toList());
  }

  @Transactional
  public void procesarColeccionesPendientes() {
    List<Coleccion> coleccionesPendientes = coleccionRepository.findByEstado(EstadoColeccion.PROCESANDO);

    if (coleccionesPendientes.isEmpty())
      return;

    log.info("Se encontraron {} colecciones pendientes.", coleccionesPendientes.size());

    for (Coleccion c : coleccionesPendientes) {
      try {
        c.getFuentes().forEach(fuente -> {
          List<String> idsFuentes = new ArrayList<>();
          idsFuentes.add(fuente.getId());
          eventPublisher.publishEvent(new FuentesAProcesarEvent(c.getId(), idsFuentes, false));
        });
      } catch (Exception e) {
        log.warn("No se pudo actualizar la colección {}. Se intentará luego. Error: {}", c.getId(),
            e.getMessage());
      }
    }
  }

  @Transactional
  public void actualizarEstadoColeccion(String coleccionId, EstadoColeccion nuevoEstado) {
    Coleccion coleccion = coleccionRepository.findById(coleccionId)
        .orElseThrow(() -> new EntityNotFoundException("Coleccion no encontrada"));
    coleccion.setEstado(nuevoEstado);
    coleccionRepository.save(coleccion);
  }

  @Transactional(readOnly = true)
  public Coleccion getColeccion(String coleccionId) {
    return coleccionRepository.findById(coleccionId)
        .orElseThrow(() -> new EntityNotFoundException("Coleccion con id " + coleccionId + " no encontrada"));
  }

  @Transactional(readOnly = true)
  public Integer solicitudesSpamPorColeccion(String coleccionId) {
    Set<Hecho> hechos = this.getColeccion(coleccionId).getHechos();
    AtomicReference<Integer> cantidadSolicitudesSpam = new AtomicReference<>(0);
    hechos.forEach(h -> {
      cantidadSolicitudesSpam
          .getAndSet(cantidadSolicitudesSpam.get() + solicitudService.cantidadSolicitudesSpam(h.getId()));
    });
    return cantidadSolicitudesSpam.get();
  }
}