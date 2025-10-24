package ar.edu.utn.frba.dds.services;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

import ar.edu.utn.frba.dds.models.AuthResponseDTO;
import ar.edu.utn.frba.dds.models.ColeccionNuevaDto;
import ar.edu.utn.frba.dds.models.EstadisticaDto;
import ar.edu.utn.frba.dds.models.NuevaEstadisticaDto;
import ar.edu.utn.frba.dds.models.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.RolesPermisosDTO;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDetallesDto;
import ar.edu.utn.frba.dds.models.SolicitudesPaginasDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class MetamapaApiService {
  private final WebClient webClient = WebClient.builder().build();
  ;
  private final WebApiCallerService webApiCallerService;
  private final String authServiceUrl;
  private final String agregadorServiceUrl;
  private final String estadisticasServiceUrl;
  public MetamapaApiService(
      WebApiCallerService webApiCallerService,
      @Value("${auth.service.url}")
      String authServiceUrl,
      @Value("${agregador.service.url}")
      String agregadorServiceUrl,
      @Value("${estadisticas.service.url}")
      String estadisticasServiceUrl
  ) {
    this.webApiCallerService = webApiCallerService;
    this.authServiceUrl = authServiceUrl;
    this.agregadorServiceUrl = agregadorServiceUrl;
    this.estadisticasServiceUrl = estadisticasServiceUrl;
  }

  public RolesPermisosDTO getRolesPermisos(String accessToken) {
    try {
      RolesPermisosDTO response = webApiCallerService.getWithAuth(
          authServiceUrl + "/user/roles-permisos",
          accessToken,
          RolesPermisosDTO.class
      );
      return response;
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
    }
  }

  public AuthResponseDTO login(String username, String password) {
    try {
      AuthResponseDTO response = webClient
          .post()
          .uri(authServiceUrl + "/login")
          .bodyValue(Map.of(
              "username", username,
              "password", password
          ))
          .retrieve()
          .bodyToMono(AuthResponseDTO.class)
          .block();
      return response;
    } catch (WebClientResponseException e) {
      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        // Login fallido - credenciales incorrectas
        return null;
      }
      // Otros errores HTTP
      throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
    }
  }

  public Boolean register(String username, String password) {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Void> response = restTemplate.exchange(
        authServiceUrl + "/register",
        HttpMethod.POST,
        new HttpEntity<>(Map.of(
            "username", username,
            "password", password
        )),
        Void.class
    );
    System.out.println(response.getStatusCode());
    if (response.getStatusCode() == HttpStatus.OK) {
      return true;
    } else if (response.getStatusCode() == CONFLICT) {
      return false;
    }
    return false;
  }

  public void eliminarColeccion(String idColeccion) {
    webApiCallerService.delete(agregadorServiceUrl + "/colecciones/" + idColeccion);
  }

  public void actualizarColeccion(String idColeccion, ColeccionNuevaDto coleccionDto) {
    webApiCallerService.put(agregadorServiceUrl + "/colecciones/" + idColeccion, coleccionDto, Void.class);
  }

  public void crearColeccion(ColeccionNuevaDto coleccionDto) {
    ObjectMapper mapper = new ObjectMapper();
    webApiCallerService.post(agregadorServiceUrl + "/colecciones", coleccionDto, Void.class);
  }

  public ResumenActividadDto obtenerResumenActividad() {
    return webApiCallerService.get(agregadorServiceUrl + "/resumen", ResumenActividadDto.class);
  }

  public SolicitudesPaginasDto obtenerSolicitudes(int page, Boolean pendientes) {
    return webApiCallerService.get(agregadorServiceUrl + "/solicitudes?page=" + page + "&pendientes=" + pendientes, SolicitudesPaginasDto.class);
  }

  public SolicitudEliminacionDetallesDto obtenerSolicitud(Long idSolicitud) {
    return webApiCallerService.get(agregadorServiceUrl + "/solicitudes/" + idSolicitud, SolicitudEliminacionDetallesDto.class);
  }

  public void aceptarSolicitud(Long idSolicitud) {
    webApiCallerService.put(agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/aceptar", Void.class, Void.class);
  }

  public void rechazarSolicitud(Long idSolicitud) {
    webApiCallerService.put(agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/denegar", Void.class, Void.class);
  }

  public void crearEstadistica(NuevaEstadisticaDto request) {
    try {
      webApiCallerService.post(estadisticasServiceUrl, request, Void.class);
    } catch (RuntimeException e) {
      if (e.getMessage().contains("Error de conexión")) {
        throw new RuntimeException("No se pudo conectar con el servicio externo. Por favor, intentá más tarde.");
      }

      if (e.getCause() instanceof WebClientResponseException wcre) {
        if (wcre.getStatusCode().equals(CONFLICT)) {
          throw new IllegalArgumentException("Ya existe una estadística sobre la coleccion con la categoria ingresada.");
        }
      }

      throw new RuntimeException("Error al crear estadística: " + e.getMessage());
    }
  }

  public List<EstadisticaDto> obtenerEstadisticas() {
    return this.webApiCallerService.getList(estadisticasServiceUrl, EstadisticaDto.class);
  }

  public SolicitudesPaginasDto obtenerSolicitudesCreadasPor(int page, Boolean pendientes) {
    return webApiCallerService.get(agregadorServiceUrl + "/solicitudes?filterByCreator=true&page=" + page + "&pendientes=" + pendientes, SolicitudesPaginasDto.class);
  }
}
