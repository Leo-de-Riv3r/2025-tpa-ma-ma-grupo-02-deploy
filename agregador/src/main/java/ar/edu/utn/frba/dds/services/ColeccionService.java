package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.ColeccionDTOSalidaGQL;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDto;
import ar.edu.utn.frba.dds.models.dtos.output.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.models.entities.utils.ColeccionConverter;
import ar.edu.utn.frba.dds.models.entities.utils.FuenteConverter;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.IOrigenRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ColeccionService {
  private final IColeccionRepository coleccionRepository;
  private final SolicitudService solicitudService;
  private final IHechoRepository hechoRepository;
  private final IOrigenRepository origenRepo;
  private final FuenteConverter fuenteConverter;
  private final ColeccionConverter coleccionConverter;
  private final HechoConverter hechoConverter;
  private final IFuenteRepository fuenteRepository;
  private final ISolicitudRepository solicitudRepository;
  private final WebClient webClient;
  @PersistenceContext
  private EntityManager em;

  public ColeccionService(IColeccionRepository coleccionRepository, SolicitudService solicitudService, IHechoRepository hechoRepository, IOrigenRepository origenRepo, FuenteConverter fuenteConverter, ColeccionConverter coleccionConverter, HechoConverter hechoConverter, IFuenteRepository fuenteRepository, ISolicitudRepository solicitudRepository, WebClient.Builder webClientBuilder) {
    this.coleccionRepository = coleccionRepository;
    this.solicitudService = solicitudService;
    this.hechoRepository = hechoRepository;
    this.origenRepo = origenRepo;
    this.fuenteConverter = fuenteConverter;
    this.coleccionConverter = coleccionConverter;
    this.hechoConverter = hechoConverter;
    this.fuenteRepository = fuenteRepository;
    this.solicitudRepository = solicitudRepository;
    this.webClient = webClientBuilder.build();
  }

  public ColeccionDTOSalida createColeccion(ColeccionDTOEntrada dto) {
    log.info("Creando nueva coleccion");
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
        Fuente fuenteFinal;
        Fuente fuente = fuenteConverter.fromDto(fuenteDTO);
        Optional<Fuente> fuenteExistente = fuenteRepository.findByUrlAndTipoFuente(fuente.getUrl(), fuente.getTipoFuente());
        if (fuenteExistente.isPresent()) {
          fuenteFinal = fuenteExistente.get();
          this.refrescarYNormalizarHechos(fuenteFinal);
          fuenteFinal = fuenteRepository.save(fuenteFinal);
          System.out.println("FUENTE EXISTENTE");
        } else {
          //traigo hechos y normalizo
          this.refrescarYNormalizarHechos(fuente);
          fuenteFinal = fuenteRepository.save(fuente);
          System.out.println("FUENTE GUARDADA");
        }
        fuentes.add(fuenteFinal);
      });
      coleccion.setFuentes(fuentes);
    }

    if(dto.getFiltros() != null) {
      Set<IFiltroStrategy> filtros = dto.getFiltros().stream().map(dtoFiltro -> FiltroStrategyFactory.fromDTO(dtoFiltro)).collect(Collectors.toSet());
      filtros.forEach(f -> coleccion.addCriterio(f));
      if (!coleccion.getFuentes().isEmpty()) {
        coleccion.actualizarHechosFiltrados();
      }
    }
    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);

    log.info("EVENTO_CREACIÓN - Colección creada exitosamente. ID: {}, Título: '{}'",
        coleccionGuardada.getId(),
        coleccionGuardada.getTitulo());
    return coleccionConverter.fromEntity(coleccionGuardada);
  }

  public List<ColeccionDTOSalida> getColeccionesDTO() {
    List<Coleccion> colecciones = coleccionRepository.findAll();
    return colecciones.stream().map(c -> coleccionConverter.fromEntity(c)).toList();
  }

  public List<Coleccion> getColecciones() {
    return coleccionRepository.findAll();
  }

  public ColeccionDTOSalida getColeccionDTO(String coleccionId) {
    Coleccion coleccion = this.getColeccion(coleccionId);
    ColeccionDTOSalida respuesta = coleccionConverter.fromEntity(coleccion);
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
        Fuente fuente = fuenteConverter.fromDto(fuenteDTO);
        Optional<Fuente> fuenteExistente = fuenteRepository.findByUrlAndTipoFuente(fuente.getUrl(), fuente.getTipoFuente());
        if (fuenteExistente.isPresent()) {
          fuente = fuenteExistente.get();
          this.refrescarYNormalizarHechos(fuente);
        } else {
          //traigo hechos y normalizo
          this.refrescarYNormalizarHechos(fuente);
        }
        fuentes.add(fuente);
      });
      coleccion.limpiarFuentes();
      coleccion.setearFuentes(fuentes);
    } else {
      coleccion.limpiarFuentes();
    }
    if (dto.getAlgoritmo() != null && !dto.getAlgoritmo().isBlank()) {
      try {
        TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmo().toUpperCase());
        IConsensoStrategy algoritmoConsenso = tipoAlgoritmo.getStrategy();
        coleccion.setAlgoritmoConsenso(algoritmoConsenso);
      } catch (Exception e){
        throw new IllegalArgumentException("Algoritmo de tipo " + dto.getAlgoritmo() + " no aceptado");
      }
    }

    if(dto.getFiltros() != null) {
      Set<IFiltroStrategy> filtros = dto.getFiltros().stream().map(dtoFiltro -> FiltroStrategyFactory.fromDTO(dtoFiltro)).collect(Collectors.toSet());

      coleccion.setearCriterios(filtros);

      if (!coleccion.getFuentes().isEmpty()) {
        coleccion.actualizarHechosFiltrados();
      }
    }

    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);
    log.info("EVENTO_MODIFICACIÓN - Colección actualizada. ID: {}, Titulo: '{}'", coleccionGuardada.getId()
    , coleccionGuardada.getTitulo());
  }

  public void deleteColeccion(String coleccionId) {
    coleccionRepository.deleteById(coleccionId);
    log.info("EVENTO_ELIMINACION - Colección eliminada. ID: {}", coleccionId);
  }

  @Transactional
  public void refrescoFuentes() {
    List<Fuente> fuentes = fuenteRepository.findAll();
    if (!fuentes.isEmpty()){
      try {
        fuentes.forEach(f -> this.refrescarYNormalizarHechos(f));
      } catch (Exception e ) {

      }
    }
    fuenteRepository.saveAll(fuentes);
  }

  @Transactional
  public void refrescarYNormalizarHechos(Fuente fuente) {
    Set<Hecho> hechos = fuente.obtenerHechosRefrescados(hechoConverter, webClient);
    System.out.println("fuente url: " + fuente.getUrl());
    System.out.println("Cantidad de hechos encontrados: " + hechos.size());

    hechos.forEach(h -> {
      Optional<Hecho> hechoExistente = hechoRepository
          .findByTituloAndDescripcionAndFechaAcontecimiento(
              h.getTitulo(), h.getDescripcion(), h.getFechaAcontecimiento()
          );

      Hecho hechoFinal;
      if (hechoExistente.isPresent()) {
        hechoFinal = hechoExistente.get();
      } else {
        origenRepo.findByTipoAndAutor(
            h.getOrigen().getTipo(), h.getOrigen().getAutor()
        ).ifPresent(h::setOrigen);

        hechoRepository.buscarCategoriaNormalizada(h.getCategoria())
            .ifPresent(h::setCategoria);

        hechoFinal = hechoRepository.save(h);
      }
      fuente.addHecho(hechoFinal);
    });
  }


  public PaginacionDto<HechoDtoSalida> getHechos(String coleccionId, boolean navegacionCurada, Integer page, Set<IFiltroStrategy> filtros) {
    Set<Hecho> hechos = new LinkedHashSet<>();
    if (coleccionId != null) {
      Optional<Coleccion> coleccion = coleccionRepository.findById(coleccionId);
      if (coleccion.isEmpty()) {
        throw new EntityNotFoundException("Coleccion con id " + coleccionId + " no encontrada");
      }
      if (navegacionCurada) {
        hechos = coleccion.map(Coleccion::getHechosCurados).orElse(null);
      } else {
        hechos = coleccion.map(Coleccion::getHechos).orElse(null);
      }
    }

    if (coleccionId == null ) {
      //List<Hecho> hechosBusqeda = hechoRepository.busquedaTexto(textoTitulo);
      List<Coleccion> colecciones = coleccionRepository.findAll();
      Set<Hecho> hechosColecciones = new HashSet<>();
      colecciones.forEach(c -> hechosColecciones.addAll(c.getHechos()));
      hechos.addAll(hechosColecciones);
    }

    if (filtros != null) {
      hechos = hechos != null ? hechos.stream()
          .filter(h -> h.cumpleFiltros(filtros))
          .collect(Collectors.toSet()) : null;
    }

    hechos = hechos.stream().filter(hecho -> !solicitudService.hechoEliminado(hecho)).collect(Collectors.toSet());
    hechos = filtrarDuplicados(hechos);

    // Excluir hechos eliminados y duplicados
    hechos = hechos.stream()
        .filter(h -> !solicitudService.hechoEliminado(h))
        .collect(Collectors.toSet());

    hechos = filtrarDuplicados(hechos);

    // Convertir a lista para poder paginar
    List<Hecho> hechosList = new ArrayList<>(hechos);
    hechosList.sort(Comparator.comparing(Hecho::getId));
    // Configuración de paginación
    int size = 500;
    int totalElements = hechos.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    int currentPage = (page == null || page < 1) ? 1 : page;
    if (currentPage > totalPages && totalPages > 0) {
      currentPage = totalPages; // Si la página pedida es mayor que la última, usamos la última
    }
    // Calcular índices para sublista
    int fromIndex = (currentPage - 1) * size;
    int toIndex = Math.min(fromIndex + size, totalElements);

    List<HechoDtoSalida> hechosPaginados = new ArrayList<>();
    if (fromIndex < totalElements) {
      hechosPaginados = hechosList.subList(fromIndex, toIndex)
          .stream()
          .map(hecho -> hechoConverter.fromEntity(hecho))
          .collect(Collectors.toList());
    }

    return new PaginacionDto<>(
        hechosPaginados,
        currentPage,
        totalPages
    );

  }

    public static Set<Hecho> filtrarDuplicados(Set<Hecho> hechos) {
      Set<String> vistos = new HashSet<>();
      Set<Hecho> resultado = new HashSet<>();

      for (Hecho hecho : hechos) {
        // Genero una "clave única" combinando los atributos relevantes
        String clave = hecho.getTitulo() + "|"
            + hecho.getDescripcion() + "|"
            + hecho.getCategoria() + "|"
            + hecho.getFechaAcontecimiento();

        if (!vistos.contains(clave)) {
          vistos.add(clave);
          resultado.add(hecho);
        }
      }

      return resultado;
    }


  public void addFuente(String coleccionId, FuenteDTO dto) {
    Fuente fuente = fuenteConverter.fromDto(dto);
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

    colecciones.forEach(coleccion ->  coleccion.refrescarHechosCurados(em));
    //elimino los hechos con fuente_id nulo porque ya son obsoletos
    coleccionRepository.saveAll(colecciones);
  }
  @Transactional
  public void addCriterio(String id, IFiltroStrategy filtro) {
    Coleccion coleccion = this.getColeccion(id);
    coleccion.addCriterio(filtro);
    if (!coleccion.getHechos().isEmpty()) {
      coleccion.actualizarHechosFiltrados();
    }
    coleccionRepository.save(coleccion);
  }

  public HechoDetallesDtoSalida getHechoDto(Long idHecho) {
    Hecho hecho = this.getHechoById(idHecho);
    return hechoConverter.fromEntityDetails(hecho);
  }

  public HechoDetallesDtoSalida getHechoDtoDetalles(Long idHecho) {
    Hecho hecho = this.getHechoById(idHecho);
    return hechoConverter.fromEntityDetails(hecho);
  }

  Hecho getHechoById(Long idHecho) {
    return hechoRepository.findById(idHecho)
        .orElseThrow(() -> new EntityNotFoundException("Hecho con id " + idHecho + " no encontrada"));
  }

  public void refrescarHechosFiltrados() {
    List <Coleccion> colecciones = coleccionRepository.findAll();
    colecciones.forEach(Coleccion::actualizarHechosFiltrados);
    coleccionRepository.saveAll(colecciones);
  }

  public ResumenActividadDto getResumenActividad() {
    ResumenActividadDto resumenActividadDto = new ResumenActividadDto();
    resumenActividadDto.setHechostotales(hechoRepository.count());
    resumenActividadDto.setFuentesTotales(fuenteRepository.count());
    resumenActividadDto.setSolicitudesEliminacion(solicitudRepository.count());
    return resumenActividadDto;
  }

  public ColeccionDTOSalidaGQL getColeccionOutputDto(String id, Boolean curadosFinal, Integer page, Set<IFiltroStrategy> filtros) {
    Coleccion coleccion = this.getColeccion(id);
    ColeccionDTOSalida coleccionDto = coleccionConverter.fromEntity(coleccion);
    ColeccionDTOSalidaGQL respuesta = new ColeccionDTOSalidaGQL(coleccionDto);
    respuesta.setHechos(this.getHechos(id, curadosFinal, page, filtros));
    return respuesta;
  }

//  public void actualizarHecho(Long idHecho, HechoUpdateDto hechoDto) {
//    Hecho hecho = this.getHechoById(idHecho);
//    if(hechoDto.getTitulo() != null) {
//      hecho.setTitulo(hechoDto.getTitulo());
//    }
//    if(hechoDto.getDescripcion() != null) {
//      hecho.setDescripcion(hechoDto.getDescripcion());
//    }
//    if(hechoDto.getFechaHecho() != null) {
//      hecho.setFechaAcontecimiento(hechoDto.getFechaHecho().atStartOfDay());
//    }
//    if(hechoDto.getLatitud() != null) {
//      hecho.setUbicacion();
//    }
//    if(hechoDto.getMultimedia() != null) {
//
//    }
//
//    hechoRepository.save(hecho);
//  }
}
