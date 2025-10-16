package ar.edu.utn.frba.dds.services;

import static org.springframework.web.reactive.function.server.RequestPredicates.queryParam;

import ar.edu.utn.frba.dds.models.Coleccion;
import ar.edu.utn.frba.dds.models.ColeccionDetallesDto;
import ar.edu.utn.frba.dds.models.ColeccionNuevaDto;

import ar.edu.utn.frba.dds.models.FiltrosDto;
import ar.edu.utn.frba.dds.models.HechoDetallesDto;
import ar.edu.utn.frba.dds.models.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDetallesDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDto;
import ar.edu.utn.frba.dds.models.SolicitudesPaginasDto;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
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
  private String urlBase = "http://localhost:5010";
  private final RestTemplate restTemplate;
  private WebClient webClient = WebClient.builder().baseUrl("http://localhost:5010").build();

  public AgregadorService(MetamapaApiService metamapaApiService) {
    this.metamapaApiService = metamapaApiService;
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
    try {
      ResponseEntity<List<Coleccion>> response = restTemplate.exchange(
          urlBase + "/colecciones",
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<List<Coleccion>>() {
          }
      );
      return response.getBody();
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener colecciones");
    }
  }

  public ColeccionDetallesDto getHechosColeccion(String idColeccion, FiltrosDto filtros, int page) {
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(urlBase + "/colecciones/" + idColeccion + "/hechos")
        .queryParam("page", page);

    // Agregar los parámetros solo si no son nulos o vacíos
    if (filtros.getCategoria() != null && !filtros.getCategoria().isEmpty()) {
      builder.queryParam("categoria", filtros.getCategoria());
    }
    if (filtros.getProvincia() != null && !filtros.getProvincia().isEmpty()) {
      builder.queryParam("provincia", filtros.getProvincia());
    }
    if (filtros.getMunicipio() != null && !filtros.getMunicipio().isEmpty()) {
      builder.queryParam("municipio", filtros.getMunicipio());
    }
    if (filtros.getDepartamento() != null && !filtros.getDepartamento().isEmpty()) {
      builder.queryParam("departamento", filtros.getDepartamento());
    }
    if (filtros.getCurados() != null && !filtros.getCurados().isEmpty()) {
      builder.queryParam("curados", filtros.getCurados().equalsIgnoreCase("Si"));
    }
    if (filtros.getFecha_acontecimiento_desde() != null) {
      builder.queryParam("fecha_acontecimiento_desde", filtros.getFecha_acontecimiento_desde());
    }
    if (filtros.getFecha_acontecimiento_hasta() != null) {
      builder.queryParam("fecha_acontecimiento_hasta", filtros.getFecha_acontecimiento_hasta());
    }

    // Si tu paginación también viene en el filtro:
//    if (filtros.getPage() != null) {
//      builder.queryParam("page", filtros.getPage());
//    }

    // Construir la URL final
    String url = builder.toUriString();

    ResponseEntity<ColeccionDetallesDto> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        ColeccionDetallesDto.class
    );

    return response.getBody();
  }

  public void crearColeccion(ColeccionNuevaDto coleccionNueva) {
    if (coleccionNueva.getAlgoritmo().isBlank()) coleccionNueva.setAlgoritmo(null);
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
//    ResponseEntity<Void> response = restTemplate.exchange(
//        urlBase + "/colecciones/" + idColeccion,
//        HttpMethod.PUT,
//        new HttpEntity<>(coleccion),
//        Void.class
//    );
    metamapaApiService.actualizarColeccion(idColeccion, coleccion);
  }

  public void eliminarColeccion(String idColeccion) {
//    ResponseEntity<Void> response = restTemplate.exchange(
//        urlBase + "/colecciones/" + idColeccion,
//        HttpMethod.DELETE,
//        null,
//        Void.class
//    );
    metamapaApiService.eliminarColeccion(idColeccion);
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

  public ResumenActividadDto obtenerResumenActividad() {
    return metamapaApiService.obtenerResumenActividad();
  }

  public SolicitudesPaginasDto obtenerSolicitudes(int page, Boolean pendientes) {
    return metamapaApiService.obtenerSolicitudes(page, pendientes);
  }

  public SolicitudEliminacionDetallesDto obtenerSolicitud(Long idSolicitud) {
    return metamapaApiService.obtenerSolicitud(idSolicitud);
  }

  public void aceptarSolicitud(Long idSolicitud) {
    metamapaApiService.aceptarSolicitud(idSolicitud);
  }

  public void rechazarSolicitud(Long idSolicitud) {
    metamapaApiService.rechazarSolicitud(idSolicitud);
  }

  public SolicitudesPaginasDto obtenerSolicitudesCreadasPor(int page, Boolean pendientes) {
    return metamapaApiService.obtenerSolicitudesCreadasPor(page, pendientes);
  }
}
