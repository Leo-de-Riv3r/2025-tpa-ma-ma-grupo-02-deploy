package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.HechoManualDTO;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FuenteDinamicaService {
  private final MetamapaApiService metamapaApiService;
  private final RestTemplate restTemplate;
  private final String fuenteDinamicaServiceUrl;
  public FuenteDinamicaService(
      MetamapaApiService metamapaApiService, RestTemplate restTemplate, @Value("${fuenteDinamica.service.url}")
     String fuenteDinamicaServiceUrl) {
    this.metamapaApiService = metamapaApiService;
    this.restTemplate = restTemplate;
    this.fuenteDinamicaServiceUrl = fuenteDinamicaServiceUrl;
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
    System.out.println("hechos pendientes: " + hechosPendientes.size());
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
