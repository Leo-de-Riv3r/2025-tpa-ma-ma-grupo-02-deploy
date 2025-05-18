package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ColeccionesService {
  private WebClient webClient;
  private IColeccionesRepository coleccionesRepository;

  public void refrescoColecciones(){
    //una posible solucion a fuente proxy seria que las fuentes tengan el mensaje tiempoReal()
    //si la fruente agregada es en tiempo real, se conectara inmediatamente a la fuente
    List <Coleccion> colecciones = coleccionesRepository.getColecciones().forEach(coleccion -> actualizarFuentes(coleccion));
    colecciones.forEach(coleccion -> actualizarColeccion(coleccion.getHandler(), coleccion));
  }

  public void actualizarColeccion(String handler, Coleccion coleccion) {
    coleccionesRepository.updateColeccion(handler, coleccion);
  }

  private void actualizarFuentes(Coleccion coleccion){
    coleccion.getFuentes().forEach(fuente -> {
      List<Hecho> hechosActualizados = consultarHechos(fuente.getUrl());
      fuente.setHechos(hechosActualizados);
    });


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