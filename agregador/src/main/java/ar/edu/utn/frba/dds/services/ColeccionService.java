package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.CambioAlgoritmoDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
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
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class ColeccionService {
  private final IColeccionRepository coleccionRepository;
  private final SolicitudService solicitudService;
  private final IHechoRepository hechoRepository;
  private final FuenteConverter fuenteConverter;
  private final ColeccionConverter coleccionConverter;
  private final HechoConverter hechoConverter;
  @PersistenceContext
  private EntityManager em;

  public ColeccionService(IColeccionRepository coleccionRepository, SolicitudService solicitudService, IHechoRepository hechoRepository, FuenteConverter fuenteConverter, ColeccionConverter coleccionConverter, HechoConverter hechoConverter) {
    this.coleccionRepository = coleccionRepository;
    this.solicitudService = solicitudService;
    this.hechoRepository = hechoRepository;
    this.fuenteConverter = fuenteConverter;
    this.coleccionConverter = coleccionConverter;
    this.hechoConverter = hechoConverter;
  }

  public ColeccionDTOSalida createColeccion(ColeccionDTOEntrada dto) {
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
        Fuente fuente = fuenteConverter.fromDto(fuenteDTO);
        fuentes.add(fuente);
      });
      coleccion.setFuentes(fuentes);
    }

    if(dto.getFiltros() != null) {
      Set<IFiltroStrategy> filtros = dto.getFiltros().stream().map(dtoFiltro -> FiltroStrategyFactory.fromDTO(dtoFiltro)).collect(Collectors.toSet());
      filtros.forEach(f -> coleccion.addCriterio(f));
    }
    Coleccion coleccionGuardada = coleccionRepository.save(coleccion);
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
    System.out.println("ACTUALIZAR COLECCION");
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

  @Transactional
  public void refrescoColecciones() {
    List <Coleccion> colecciones = this.getColecciones();
    colecciones.forEach(coleccion -> coleccion.getFuentes().forEach(fuente -> {
      fuente.refrescarHechos(hechoConverter);
      Set<Hecho> hechosFuente = fuente.getHechos();

      hechosFuente.forEach(h -> {
        //hecho nuevo no persistido
          //logica para hallar categoria
          Optional<String> categoriaEncontrada = hechoRepository.buscarCategoriaNormalizada(h.getCategoria());
          if (categoriaEncontrada.isPresent()) {
            h.setCategoria(categoriaEncontrada.get());
          }
      });
    }));
    this.coleccionRepository.saveAll(colecciones);
  }

  public Set<Hecho> getHechos(String coleccionId, boolean navegacionCurada, Integer page, Integer perPage, Set<IFiltroStrategy> filtros, String textoTitulo) {
    Set<Hecho> hechos = Set.of();
    if (coleccionId != null) {
      Optional<Coleccion> coleccion = coleccionRepository.findById(coleccionId);
      if (navegacionCurada) {
        hechos = coleccion.map(Coleccion::getHechosCurados).orElse(null);
      } else {
        hechos = coleccion.map(Coleccion::getHechos).orElse(null);
      }
    }

    if (coleccionId == null ) {
      //List<Hecho> hechosBusqeda = hechoRepository.busquedaTexto(textoTitulo);
    }

    if (page != null && perPage != null) {
      hechos =  hechos != null ? hechos.stream()
          .skip((long) (page - 1) * perPage)
          .limit(perPage)
          .collect(HashSet::new, HashSet::add, HashSet::addAll) : null;
    }

    if (filtros != null) {
      hechos = hechos != null ? hechos.stream()
          .filter(h -> h.cumpleFiltros(filtros))
          .collect(Collectors.toSet()) : null;
    }

    //Set<Hecho> finalHechos = hechos;
    //return hechos.stream().filter(h -> hechoRepetido(h)).collect(Collectors.toSet());
    hechos = hechos.stream().filter(hecho -> !solicitudService.hechoEliminado(hecho)).collect(Collectors.toSet());
    return filtrarDuplicados(hechos);
    //filtrar hechos que no esten repetidos para mejorar las vistas
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
