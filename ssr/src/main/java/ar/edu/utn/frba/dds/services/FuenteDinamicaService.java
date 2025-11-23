package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.HechoManualDTO;
import ar.edu.utn.frba.dds.models.HechoUpdateDTO;
import ar.edu.utn.frba.dds.models.RevisionHechoDto;
import ar.edu.utn.frba.dds.models.SolicitudHechoDto;
import ar.edu.utn.frba.dds.models.SolicitudHechoInputDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FuenteDinamicaService {
  private final MetamapaApiService metamapaApiService;
  private final RestTemplate restTemplate;
  private final String fuenteDinamicaServiceUrl;
  private final WebApiCallerService webApiCallerService;
  public FuenteDinamicaService(
      MetamapaApiService metamapaApiService, RestTemplate restTemplate, @Value("${fuenteDinamica.service.url}")
     String fuenteDinamicaServiceUrl, WebApiCallerService webApiCallerService) {
    this.metamapaApiService = metamapaApiService;
    this.restTemplate = restTemplate;
    this.fuenteDinamicaServiceUrl = fuenteDinamicaServiceUrl;
    this.webApiCallerService = webApiCallerService;
  }

  public HechoUpdateDTO obtenerHechoEdicion(Long id) {
    Class<HechoUpdateDTO> responseType = HechoUpdateDTO.class;

    String url = fuenteDinamicaServiceUrl + "/hechos/" + id;

    try {
      var response = restTemplate.getForEntity(url, responseType);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        throw new RuntimeException("API devolvió código de estado " + response.getStatusCode() +
            " al intentar obtener Hecho con ID " + id);
      }
    } catch (HttpClientErrorException e) {
      throw new RuntimeException("Error del cliente HTTP al acceder a " + url + ": " + e.getStatusCode(), e);
    } catch (Exception e) {
      throw new RuntimeException("Error genérico al procesar la solicitud a la API para ID " + id, e);
    }
  }

  public void editarHecho(Long id, HechoUpdateDTO hechoDto, List<MultipartFile> multimedia) {
    if (hechoDto.getTitulo() == null || hechoDto.getTitulo().isBlank()) {
      throw new IllegalArgumentException("DEBUG: El título del DTO es nulo o vacío después del post.");
    }
    if (hechoDto.getLatitud() == null || hechoDto.getLongitud() == null) {
      throw new IllegalArgumentException("DEBUG: Latitud o Longitud son nulas. Revise el JS y los IDs del HTML.");
    }

    String targetUrl = fuenteDinamicaServiceUrl + "/hechos/" + id;
    System.out.println("DEBUG URL de Edición: " + targetUrl);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<HechoUpdateDTO> jsonPart = new HttpEntity<>(hechoDto, jsonHeaders);
    body.add("hecho", jsonPart);

    if (multimedia != null && !multimedia.isEmpty()) {
      for (MultipartFile file : multimedia) {
        if (file.isEmpty()) continue;

        try {
          ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return file.getOriginalFilename();
            }
          };
          body.add("multimedia", fileResource);
        } catch (Exception e) {
          throw new RuntimeException("Error al procesar archivo para la API", e);
        }
      }
    }

    try {
      webApiCallerService.put(
          targetUrl,
          body,
          Void.class
      );
    } catch (Exception e) {
      throw new RuntimeException("ERROR durante PUT Multipart a la API de Hechos: " + e.getMessage(), e);
    }
  }

  public void  crearHecho(HechoManualDTO hechoDto, List<MultipartFile> multimedia) {
    // --- 1. Crear el cuerpo de la petición Multipart ---
    // Esto contendrá las diferentes "partes" (el JSON y los archivos)
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // --- 2. Añadir la parte "hecho" (el JSON) ---
    // Tomamos el DTO que recibimos (poblado por @ModelAttribute)
    // y lo envolvemos para que se envíe como 'application/json'.
    // Esta es la "traducción" clave.
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<HechoManualDTO> jsonPart = new HttpEntity<>(hechoDto, jsonHeaders);

    // La API externa espera una parte llamada "hecho"
    body.add("hecho", jsonPart);

    // --- 3. Añadir la parte "multimedia" (los archivos) ---
    if (multimedia != null && !multimedia.isEmpty()) {
      for (MultipartFile file : multimedia) {
        if (file.isEmpty()) continue;

        try {
          // Creamos un recurso desde los bytes del archivo
          ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              // Es vital sobreescribir 'getFilename'
              return file.getOriginalFilename();
            }
          };
          // La API externa espera partes llamadas "multimedia"
          body.add("multimedia", fileResource);
        } catch (Exception e) {
          throw new RuntimeException("Error al procesar archivo para la API", e);
        }
      }
    }

    // --- 4. Crear los Headers de la petición principal ---
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // --- 5. Ensamblar y enviar la petición ---
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    try {
      // Hacemos el POST a la API externa
      ResponseEntity<Void> response = restTemplate.postForEntity(
          fuenteDinamicaServiceUrl + "/hechos",
          requestEntity,
          Void.class
      );

    } catch (Exception e) {
      // Manejo básico de errores (la API está caída, devuelve 500, etc.)
      throw new RuntimeException("Error al comunicarse con la API de Hechos: " + e.getMessage(), e);
    }
  }

  public List<SolicitudHechoDto> obtenerSolicitudesHecho() {
    List<SolicitudHechoDto> hechosPendientes = metamapaApiService.obtenerSolicitudesHecho();
    System.out.println("hechos pendientes fuente dinaica : " + hechosPendientes.size());
    return hechosPendientes;
  }

  public SolicitudHechoInputDto obtenerSolicitudById(Long idHecho) {
    return metamapaApiService.obtenerSolicitudHechoById(idHecho);
  }

  public void aceptarSolicitud(Long idHecho, RevisionHechoDto revisionHechoDto) {
    metamapaApiService.aceptarSolicitudHecho(idHecho, revisionHechoDto);
  }

  public void rechazarSolicitud(Long idHecho, RevisionHechoDto revisionHechoDto) {
    metamapaApiService.rechazarSolicitudHecho(idHecho, revisionHechoDto);
  }

  public void aceptarConSugerencias(Long idHecho, RevisionHechoDto revisionHechoDto) {
    metamapaApiService.aceptarSolicitudConSugerencias(idHecho, revisionHechoDto);
  }

  public List<SolicitudHechoDto> obtenerHechosPorCreador() {
    return metamapaApiService.obtenerHechosPorCreador();
  }
}
