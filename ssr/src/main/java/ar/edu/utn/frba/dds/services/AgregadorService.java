package ar.edu.utn.frba.dds.services;

import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

import ar.edu.utn.frba.dds.models.Coleccion;
import ar.edu.utn.frba.dds.models.ColeccionDetallesDto;
import ar.edu.utn.frba.dds.models.ColeccionHechosDto;
import ar.edu.utn.frba.dds.models.ColeccionNuevaDto;

import ar.edu.utn.frba.dds.models.FiltrosDto;
import ar.edu.utn.frba.dds.models.HechoDetallesDto;
import ar.edu.utn.frba.dds.models.PaginacionDtoHechoDtoSalida;
import ar.edu.utn.frba.dds.models.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDetallesDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDto;
import ar.edu.utn.frba.dds.models.SolicitudesPaginasDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class AgregadorService {
  private final MetamapaApiService metamapaApiService;
  private final HttpGraphQlClient gqlAgregadorClient;
  @Value("${agregador.service.url}")
  private String urlBase;
  @Value("${fuenteDinamica.service.url}")
  private String fuenteDinamicaUrl;
  private final RestTemplate restTemplate;
  @Value("${fuenteEstatica.service.url}")
  private String fuenteEstaticaUrl;


  public AgregadorService(MetamapaApiService metamapaApiService, HttpGraphQlClient gqlAgregadorClient, RestTemplate restTemplate) {
    this.metamapaApiService = metamapaApiService;
    this.gqlAgregadorClient = gqlAgregadorClient;
    this.restTemplate = restTemplate;
  }

  public List<Coleccion> obtenerColecciones() {
      return gqlAgregadorClient.documentName("getColecciones")
          .retrieve("colecciones")
          .toEntityList(Coleccion.class).block();
  }

  public ColeccionHechosDto getHechosColeccion(String idColeccion, FiltrosDto filtros, int page) {
    return gqlAgregadorClient.documentName("getHechosColeccion")
        .variable("id", idColeccion)
        .variable("page", page)
        .variable("filtro", filtros)
        .variable("curados", (filtros != null && filtros.getCurados() != null && filtros.getCurados().equalsIgnoreCase("Si") ))
        .retrieve("coleccion")
        .toEntity(ColeccionHechosDto.class).block();
  }

  public void crearColeccion(ColeccionNuevaDto coleccionNueva) {
    if (coleccionNueva.getAlgoritmo().isBlank()) coleccionNueva.setAlgoritmo(null);
    //check if coleccionNueva has fuenteDinamica
    if (coleccionNueva.getFuentes() != null) {
      coleccionNueva.getFuentes().forEach(f -> {
        if (f.getTipoFuente().equals("DINAMICA")) {
          f.setUrl(fuenteDinamicaUrl);
        } else if (f.getTipoFuente().equals("ESTATICA")){
          f.setUrl(fuenteEstaticaUrl + "/" + f.getUrl());
        }
        System.out.println("url de fuente enviada: " + f.getUrl());
    });

    }

    metamapaApiService.crearColeccion(coleccionNueva);
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
    if (coleccion.getAlgoritmo().isBlank()) coleccion.setAlgoritmo(null);
    if (coleccion.getFuentes() != null) {
      coleccion.getFuentes().forEach(f -> {
        if (f.getTipoFuente().equals("DINAMICA")) {
          f.setUrl(fuenteDinamicaUrl);
        } else if (f.getTipoFuente().equals("ESTATICA") && (!f.getUrl().contains(fuenteEstaticaUrl))){
          f.setUrl(fuenteEstaticaUrl + "/" + f.getUrl());
        }
        System.out.println("url de fuente enviada: " + f.getUrl());
      });

    }

    restTemplate.exchange(
        urlBase + "/colecciones/" + idColeccion,
        HttpMethod.PUT,
        new HttpEntity<>(coleccion),
        Void.class
    );
    //metamapaApiService.actualizarColeccion(idColeccion, coleccion);
  }

  public void eliminarColeccion(String idColeccion) {
    metamapaApiService.eliminarColeccion(idColeccion);
  }

  public HechoDetallesDto getDetallesHecho(Long idHecho) {
    return gqlAgregadorClient.documentName("getHechoById")
        .variable("id", idHecho)
        .retrieve("hecho")
        .toEntity(HechoDetallesDto.class).block();
  }

  public void enviarSolicitud(SolicitudEliminacionDto solicitud) {
    ResponseEntity<Void> response = restTemplate.exchange(
        urlBase + "/solicitudes",
        HttpMethod.POST,
        new HttpEntity<>(solicitud),
        Void.class
    );
  }

  public ResumenActividadDto obtenerResumenActividad() {
    return metamapaApiService.obtenerResumenActividad();
  }

  public SolicitudesPaginasDto obtenerSolicitudes(int page, Boolean pendientes) {
    return metamapaApiService.obtenerSolicitudes(page, pendientes);
  }

  public SolicitudEliminacionDetallesDto obtenerSolicitud(Long idSolicitud) {
    return gqlAgregadorClient.documentName("getSolicitudEliminacion")
        .variable("id", idSolicitud)
        .retrieve("solicitud")
        .toEntity(SolicitudEliminacionDetallesDto.class).block();
  }

  public void aceptarSolicitud(Long idSolicitud) {
    metamapaApiService.aceptarSolicitud(idSolicitud);
  }

  public void rechazarSolicitud(Long idSolicitud) {
    metamapaApiService.rechazarSolicitud(idSolicitud);
  }

  public SolicitudesPaginasDto obtenerSolicitudesCreadasPor(int page, Boolean pendientes) {
    return metamapaApiService.obtenerSolicitudesCreadasPor(page, pendientes);
//    return httpGraphQlClient.documentName("getSolicitudesEliminacionDeUsuario")
//        .variable("page", page)
//        .variable("pendientes", pendientes)
//        .variable("filterByCreator", true)
//        .retrieve("solicitudes")
//        .toEntity(SolicitudesPaginasDto.class).block();
  }
}
