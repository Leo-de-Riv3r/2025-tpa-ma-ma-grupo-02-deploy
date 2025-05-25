package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.AgregadorApplication;
import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.repositories.HechosSolicitudesRepository;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosSolicitudesRepository;
import ar.edu.utn.frba.dds.services.IAgregadorService;
import ar.edu.utn.frba.dds.services.IDetectorSpam;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AgregadorService  implements IAgregadorService{
  private IHechosSolicitudesRepository hechosSolicitudesRepository = new HechosSolicitudesRepository();
  private IDetectorSpam detectorSpam;
  private IColeccionesRepository coleccionesRepository;

  public AgregadorService(IHechosSolicitudesRepository hechosSolicitudesRepository, IDetectorSpam detectorSpam, IColeccionesRepository coleccionesRepository) {
    this.hechosSolicitudesRepository = hechosSolicitudesRepository;
    this.detectorSpam = detectorSpam;
    this.coleccionesRepository = coleccionesRepository;
  }

  @Override
  public void createSolicitud(Solicitud solicitud) {
    hechosSolicitudesRepository.createSolicitud(solicitud);
    if (!detectorSpam.esSpam(solicitud.getTexto())) {
      this.rechazarSolicitud(solicitud, "automatico");
    }
  }

  @Override
  public void rechazarSolicitud(Solicitud solicitud, String supervisor)  {
    hechosSolicitudesRepository.rechazarSolicitud(solicitud, supervisor);
  }

  @Override
  public void aceptarSolicitud(Solicitud solicitud, String supervisor) {
    hechosSolicitudesRepository.aceptarSolicitud(solicitud, supervisor);
  }

  //funciones de coleccion
  @Override
  public List<Coleccion> getColecciones(){
    return coleccionesRepository.getColecciones();
  }

  @Override
  public void refrescoColecciones() {
    List<Coleccion> colecciones = this.getColecciones();
    colecciones.forEach(coleccion -> actualizarHechosFuentes(coleccion));

    colecciones.forEach(coleccion -> setFuentesColeccion(coleccion.getHandler(), coleccion.getFuentes()));
  }

  @Override
  public void setFuentesColeccion(String handler, Set<FuenteDeDatos> fuentes) {
    this.coleccionesRepository.cambiarFuentesColeccion(handler, fuentes);
  }

  @Override
  public List<Hecho> filtrarHechosSinSolicitud(List<Hecho> hechos) {
    return hechos.stream().filter(hecho -> hechosSolicitudesRepository.hechoEliminado(hecho))
        .collect(Collectors.toList());
  }
  
  @Override
  public Hecho convertirHechoDTOAHecho(Object hecho, TipoOrigen tipoOrigen) {
    if (hecho instanceof Hecho) {
      return (Hecho) hecho;
    } else {
      //transformar hechos de fuente estatica o proxy
      HechosDTOEntrada hechoDto = (HechosDTOEntrada) hecho;
      Ubicacion ubicacion = new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud());
      Origen origen = new Origen(tipoOrigen, "---");
      return new Hecho(hechoDto.getTitulo(), hechoDto.getDescripcion(), hechoDto.getCategoria(), Set.of(), ubicacion, hechoDto.getFecha_hecho(), LocalDateTime.now(), origen, List.of());
    }
  }

  @Override
  public void actualizarHechosFuentes(Coleccion coleccion, Integer page, Integer per_page) {
    coleccion.getFuentes().forEach(fuente -> {
      if (!fuente.tiempoReal()) {
        List<Hecho> hechos = consultarHechos(fuente, page, per_page);
        fuente.setHechos(hechos);
      }
    });
}

  @Override
  public void actualizarHechosFuentes(Coleccion coleccion) {
    coleccion.getFuentes().forEach(fuente -> {
      if (!fuente.tiempoReal()) {
        List<Hecho> hechosActualizados= consultarHechos(fuente);
        //reemplazar hechos de la fuente por los nuevos hechos
        fuente.setHechos(hechosActualizados);
      }
    });
  }

  //obtener todos los hechos
  @Override
  public List<Hecho> obtenerHechos() {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);

      hechos.addAll(obtenerHechosProxy(coleccion, null, null));
    });
    return hechos.stream()
        .filter(hecho -> hechosSolicitudesRepository.hechoEliminado(hecho)).toList();
  }

@Override
public List<Hecho> obtenerHechos(Integer page, Integer per_page) {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      actualizarHechosFuentes(coleccion, page,per_page);
    });

    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);

      //tratamiento fuente proxy
      hechos.addAll(obtenerHechosProxy(coleccion, page, per_page));
    });

  return hechos.stream()
      .filter(hecho -> hechosSolicitudesRepository.hechoEliminado(hecho)).toList();
  }

  @Override
  public List<Hecho> obtenerHechos(String handler) {
    Coleccion coleccion = coleccionesRepository.findById(handler);
    List<Hecho> hechos = new ArrayList<>(List.of());
    hechos.addAll(coleccion.obtenerHechos());

    hechos.addAll(obtenerHechosProxy(coleccion, null, null));

    return hechos.stream()
        .filter(hecho -> hechosSolicitudesRepository.hechoEliminado(hecho)).toList();
  }

  @Override
  public List<Hecho> obtenerHechos(String handler, Integer page, Integer per_page) {
    Coleccion coleccion = coleccionesRepository.findById(handler);
    List<Hecho> hechos = List.of();
    actualizarHechosFuentes(coleccion, page,per_page);
    hechos.addAll(coleccion.obtenerHechos());
    hechos.addAll(obtenerHechosProxy(coleccion, page, per_page));
    return hechos.stream()
        .filter(hecho -> hechosSolicitudesRepository.hechoEliminado(hecho)).toList();
  }

  private List<Hecho> obtenerHechosProxy(Coleccion coleccion, Integer page, Integer per_page) {
    List<Hecho> hechos = new ArrayList<>(List.of());
    List<FuenteDeDatos> fuentesProxy = coleccion.getFuentes().stream()
        .filter(fuente -> fuente.tiempoReal())
        .toList();
    for (FuenteDeDatos fuenteDeDatos : fuentesProxy) {
      if (page != null && per_page != null) {
        hechos.addAll(consultarHechos(fuenteDeDatos));
      } else {
        hechos.addAll(consultarHechos(fuenteDeDatos, page, per_page));
      }
    }

    return hechos;
  }

  @Override
  public List<Hecho> consultarHechos(FuenteDeDatos fuente) {
    WebClient webClient = WebClient.builder().baseUrl(fuente.getUrl()).build();
    List<Object> hechosDTOEntrada = webClient
        .get()
        .uri("/hechos")
        .retrieve()
        .bodyToMono(FuenteResponseDTO.class)
        .map(FuenteResponseDTO::getHechosDTOEntrada).block();
    return hechosDTOEntrada.stream().map(hechoDto -> convertirHechoDTOAHecho(hechoDto, fuente.getTipoOrigen())).toList();
  }

  @Override
  public List<Hecho> consultarHechos(FuenteDeDatos fuente, Integer page, Integer per_page) {
    WebClient webClient =  WebClient.builder()
        .baseUrl(fuente.getUrl())
        .build();

    List<Object> hechosDTOEntrada = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/hechos")
            .queryParam("page", page)
            .queryParam("per_page", per_page)
            .build())
        .retrieve()
        .bodyToMono(FuenteResponseDTO.class)
        .map(FuenteResponseDTO::getHechosDTOEntrada).block();

    return hechosDTOEntrada.stream().map(hechoDto -> convertirHechoDTOAHecho(hechoDto, fuente.getTipoOrigen())).toList();
  }

  @Override
  public void agregarFuente(String handler, FuenteDeDatos fuente) {
    coleccionesRepository.agregarFuente(handler, fuente);
  }
}

