package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ColeccionesService {
  private WebClient webClient;
  private IColeccionesRepository coleccionesRepository;


  public List<Coleccion> getColecciones(){
    return coleccionesRepository.getColecciones();
  }
  public void refrescoColecciones(){
    //una posible solucion a fuente proxy seria que las fuentes tengan el mensaje tiempoReal()
    //si la fruente agregada es en tiempo real, se conectara inmediatamente a la fuente
    List <Coleccion> colecciones = this.getColecciones();
    colecciones.forEach(coleccion -> actualizarFuentes(coleccion));
    //guardo cada coleccion actualizada en el repository
    colecciones.forEach(coleccion -> actualizarColeccion(coleccion.getHandler(), coleccion));
  }

  public void actualizarColeccion(String handler, Coleccion coleccion) {
    coleccionesRepository.updateColeccion(handler, coleccion);
  }

  private void actualizarFuentes(Coleccion coleccion){
    coleccion.getFuentes().forEach(fuente -> {
      List<HechosDTOEntrada> hechosActualizadosDTO = consultarHechos(fuente.getUrl())
          .stream().filter(hecho -> !this.existeHecho(hecho)).toList();

      List<Hecho> hechosActualizados = (List<Hecho>) hechosActualizadosDTO.stream().map(hechoDTO -> convertirHechoDTOAHecho(hechoDTO));
      //agregar hechos uno por uno
      for(Hecho hecho : hechosActualizados){
        //cambiar esto en fuente
        fuente.agregarHecho(hecho);
      }
    });
  }

  private Hecho convertirHechoDTOAHecho(HechosDTOEntrada hecho) {
    Ubicacion ubicacion = new Ubicacion(hecho.getLatitud(), hecho.getLongitud());
    Origen origen = new Origen(TipoOrigen.FUENTE, "-----");
    //entidad hechos modificado
    Hecho hechoConvertido = new Hecho(hecho.getTitulo(), hecho.getDescripcion(), hecho.getCategoria(), Set.of(), ubicacion, hecho.getFecha_hecho(), hecho.getCreated_at(), origen, List.of());
    return hechoConvertido;
  }

  private Boolean existeHecho(HechosDTOEntrada hechoDTO) {
    List<Hecho> hechos = this.obtenerHechos();
    if (hechos.stream().anyMatch(hecho -> this.coincideDTOConHecho(hecho, hechoDTO))) {
      return true;
    }
    return false;
  }

  private Boolean coincideDTOConHecho(Hecho hecho, HechosDTOEntrada hechoDTO) {
    return hecho.getTitulo().equals(hechoDTO.getTitulo())
        && hecho.getDescripcion().equals(hechoDTO.getDescripcion());
  }
  public List<Hecho> obtenerHechos() {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new java.util.ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);
    });
    return hechos;
  }

  private List<HechosDTOEntrada> consultarHechos(String url) {
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    return webClient
        .get()
        .uri("Hechos")
        .retrieve()
        //como retorna la lista de hechos, debe recibirlo en una lista
        .bodyToMono(new ParameterizedTypeReference<List<HechosDTOEntrada>>() {})
        .block();
  }

  public agregarFuente(String handler, FuenteDeDatos fuente) {
    Coleccion coleccion = coleccionesRepository.findByHandler(handler);
    coleccion.agregarFuente(fuente);
    this.actualizarColeccion(handler, coleccion);
  }
//  Flux<ServerSentEvent<String>> eventStream = WebClient.create()
//      .get()
//      .uri("https://api.ejemplo.com/stream")
//      .accept(MediaType.TEXT_EVENT_STREAM)
//      .retrieve()
//      .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {});
//
//eventStream.subscribe(event -> {
//    System.out.println("Evento recibido: " + event.data());
//  });
}