package com.ddsi.utn.ba.ssr.services;

import com.ddsi.utn.ba.ssr.models.Coleccion;
import com.ddsi.utn.ba.ssr.models.ColeccionDetallesDto;
import com.ddsi.utn.ba.ssr.models.ColeccionNuevaDto;

import com.ddsi.utn.ba.ssr.models.HechoDetallesDto;
import com.ddsi.utn.ba.ssr.models.SolicitudEliminacionDto;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class AgregadorService {
  private String urlBase = "http://localhost:5010";
  private final RestTemplate restTemplate;
  private WebClient webClient = WebClient.builder().baseUrl("http://localhost:5010").build();

  public AgregadorService() {
    this.restTemplate = new RestTemplate();
  }

//  public ColeccionesDto obtenerColecciones() {
//    return this.webClient.get()
//        .uri("/colecciones")
//        .retrieve()
//        .bodyToMono(ColeccionesDto.class)
//        .block();
//  }
  public List<Coleccion> obtenerColecciones() {
    ResponseEntity<List<Coleccion>> response = restTemplate.exchange(
        urlBase + "/colecciones",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Coleccion>>() {}
    );
    return response.getBody();
  }

  public ColeccionDetallesDto getHechosColeccion(String idColeccion) {
    ResponseEntity<ColeccionDetallesDto> response = restTemplate.exchange(
        urlBase + "/colecciones/" + idColeccion + "/hechos" ,
        HttpMethod.GET,
        null,
        ColeccionDetallesDto.class
    );
    return response.getBody();
  }

  public void crearColeccion(ColeccionNuevaDto coleccionNueva) {
    if (coleccionNueva.getAlgoritmo().isBlank()) coleccionNueva.setAlgoritmo(null);
    ResponseEntity<ColeccionDetallesDto> response = restTemplate.exchange(
          urlBase + "/colecciones",
          HttpMethod.POST,
          new HttpEntity<>(coleccionNueva),
          ColeccionDetallesDto.class
    );
  }

  public Coleccion obtenerColeccionPorId(String idColeccion) {
    ResponseEntity<Coleccion> response = restTemplate.exchange(
        urlBase + "/colecciones/" + idColeccion,
        HttpMethod.GET,
        null,
        Coleccion.class
    );
    return response.getBody();
  }

  public void actualizarColeccion(String idColeccion, ColeccionNuevaDto coleccion) {
    ResponseEntity<Void> response = restTemplate.exchange(
        urlBase + "/colecciones/" + idColeccion,
        HttpMethod.PUT,
        new HttpEntity<>(coleccion),
        Void.class
    );
  }

  public void eliminarColeccion(String idColeccion) {
    ResponseEntity<Void> response = restTemplate.exchange(
        urlBase + "/colecciones/" + idColeccion,
        HttpMethod.DELETE,
        null,
        Void.class
    );
  }

  public HechoDetallesDto getDetallesHecho(Long idHecho) {
    ResponseEntity<HechoDetallesDto> response = restTemplate.exchange(
        urlBase + "/hechos/" + idHecho,
        HttpMethod.GET,
        null,
        HechoDetallesDto.class
    );
    return response.getBody();
  }

  public void enviarSolicitud(SolicitudEliminacionDto solicitud) {
    ResponseEntity<Void> response = restTemplate.exchange(
        urlBase + "/solicitudes",
        HttpMethod.POST,
        new HttpEntity<>(solicitud),
        Void.class
    );
  }
}
