package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.services.IDetectorSpam;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class DetectorSpam implements IDetectorSpam {
  private WebClient webClient;
  private String url = "https://api.oopspam.com/v1/spamdetection";
  @Override
  public boolean esSpam(String texto) {
    Boolean spam = false;

    //Mapear respuesta a un DTO
    Map responseMap = webClient.post()
        .uri(url)
        .header("X-Api-Key", "3p0mc2OfA8IkAcD1ZQHnwTVEHmVKiDQE7ljqGSig")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue({})
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    if (responseMap != null) {
      //verificar que respuesta.numberOfSpamWords > 0
    }

  }
}
