package com.ddsi.utn.ba.ssr.services;

import com.ddsi.utn.ba.ssr.models.AuthResponseDTO;
import com.ddsi.utn.ba.ssr.models.Coleccion;
import com.ddsi.utn.ba.ssr.models.ColeccionNuevaDto;
import com.ddsi.utn.ba.ssr.models.ResumenActividadDto;
import com.ddsi.utn.ba.ssr.models.RolesPermisosDTO;
import com.ddsi.utn.ba.ssr.models.SolicitudEliminacionDetallesDto;
import com.ddsi.utn.ba.ssr.models.SolicitudesPaginasDto;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.core.ParameterizedTypeReference;
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
  private final WebClient webClient = WebClient.builder().build();;
  private final WebApiCallerService webApiCallerService;
  private final String authServiceUrl;
  private final String agregadorServiceUrl;

  public MetamapaApiService(
      WebApiCallerService webApiCallerService,
      @Value("${auth.service.url}")
      String authServiceUrl,
      @Value("${agregador.service.url}")
      String agregadorServiceUrl) {
    this.webApiCallerService = webApiCallerService;
    this.authServiceUrl = authServiceUrl;
    this.agregadorServiceUrl = agregadorServiceUrl;
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
      System.out.println("ERROR HTTP");
      throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
    } catch (Exception e) {
      System.out.println("NO CONNECTION");
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
      } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
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
    webApiCallerService.post(agregadorServiceUrl + "/colecciones", coleccionDto, Void.class);
  }

  public ResumenActividadDto obtenerResumenActividad() {
    return webApiCallerService.get(agregadorServiceUrl + "/resumen", ResumenActividadDto.class);
  }

  public SolicitudesPaginasDto obtenerSolicitudes(int page) {
    return webApiCallerService.get(agregadorServiceUrl + "/solicitudes?page=" + page, SolicitudesPaginasDto.class);
  }

  public SolicitudEliminacionDetallesDto obtenerSolicitud(Long idSolicitud) {
    return webApiCallerService.get(agregadorServiceUrl + "/solicitudes/" + idSolicitud, SolicitudEliminacionDetallesDto.class);
  }

  public void aceptarSolicitud(Long idSolicitud) {
    webApiCallerService.post(agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/aceptar", null, Void.class);
  }

  public void rechazarSolicitud(Long idSolicitud) {
    webApiCallerService.post(agregadorServiceUrl + "/solicitudes/" + idSolicitud + "/denegar", null, Void.class);
  }
}
