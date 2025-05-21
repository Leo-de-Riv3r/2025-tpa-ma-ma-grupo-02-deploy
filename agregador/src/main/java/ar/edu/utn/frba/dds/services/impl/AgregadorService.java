package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.repositories.HechosSolicitudesRepository;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosSolicitudesRepository;
import ar.edu.utn.frba.dds.services.IAgregadorService;
import ar.edu.utn.frba.dds.services.IDetectorSpam;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class AgregadorService implements IAgregadorService {
  private IHechosSolicitudesRepository hechosSolicitudesRepository = new HechosSolicitudesRepository();
  private IDetectorSpam detectorSpam;
  private IColeccionesRepository coleccionesRepository;

  @Override
  public void createSolicitud(Solicitud solicitud) {
    hechosSolicitudesRepository.createSolicitud(solicitud);
    if(!detectorSpam.esSpam(solicitud.getTexto())) {
      this.rechazarSolicitud(solicitud, "automatico");
    }
  }

  @Override
  public void rechazarSolicitud(Solicitud solicitud, String supervisor)  {
    hechosSolicitudesRepository.rechazarSolicitud(solicitud, supervisor);
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
    //guardo cada coleccion actualizada en el repository
    colecciones.forEach(coleccion -> setFuentesColeccion(coleccion.getHandler(), coleccion.getFuentes()));
  }
  @Override
  public void setFuentesColeccion(String handler, Set<FuenteDeDatos> fuentes) {
    this.coleccionesRepository.cambiarFuentesColeccion(handler, fuentes);
  }

  @Override
  public void actualizarColeccion(String handler, Coleccion coleccion) {
    coleccionesRepository.updateColeccion(handler, coleccion);
  }
  @Override
  public void actualizarHechosFuentes(Coleccion coleccion){
    coleccion.getFuentes().forEach(fuente -> {
      if (!fuente.tiempoReal()) {
        List<Object> hechosActualizadosDTO = Collections.singletonList(consultarHechos(fuente.getUrl()));
        List<Hecho> hechosActualizados = (List<Hecho>) hechosActualizadosDTO.stream().map(hechoDTO -> convertirHechoDTOAHecho(hechoDTO));
        //reemplazar hechos de la fuente por los nuevos hechos
        fuente.setHechos(hechosActualizados);
      }
    });
  }

  @Override
  public Hecho convertirHechoDTOAHecho(Object hecho) {
    if (hecho instanceof Hecho) {
      return (Hecho) hecho;
    } else {
      //transformar hechos de fuente estatica
      HechosDTOEntrada hechoDto = (HechosDTOEntrada) hecho;
      Ubicacion ubicacion = new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud());
      return new Hecho(hechoDto.getTitulo(), hechoDto.getDescripcion(), hechoDto.getCategoria(), Set.of(), ubicacion, hechoDto.getFecha_hecho(), LocalDateTime.now(), null,  List.of());
    }
  }

  @Override
  public List<Hecho> obtenerHechos() {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new java.util.ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);
    });
    return hechos;
  }

  @Override
  public List<HechosDTOEntrada> consultarHechos(String url) {
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    return webClient
        .get()
        .uri("Hechos")
        .retrieve()
        //como retorna la lista de hechos, debe recibirlo en una lista
        .bodyToMono(new ParameterizedTypeReference<List<HechosDTOEntrada>>() {})
        .block();
  }

  @Override
  public void agregarFuente(String handler, FuenteDeDatos fuente) {
    coleccionesRepository.agregarFuente(handler, fuente);
    if(fuente.tiempoReal()) {
      Flux<ServerSentEvent<HechosDTOEntrada>> eventStream = WebClient.create(fuente.getUrl())
          .get()
          .uri("/stream")
          .accept(MediaType.TEXT_EVENT_STREAM)
          .retrieve()
          .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<HechosDTOEntrada>>() {});

      eventStream.subscribe(event -> {
        //System.out.println("Evento recibido: " + event.data());
        //agregar hecho a la fuente dinamica
        Hecho hecho = this.convertirHechoDTOAHecho(event);
        coleccionesRepository.agregarHechoTiempoReal(handler, fuente, hecho);
      });
    }
  }
}
