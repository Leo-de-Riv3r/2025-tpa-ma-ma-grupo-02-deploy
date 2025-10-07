package ar.edu.utn.frba.dds.externalApi;

import ar.edu.utn.frba.dds.models.dtos.LugarDTO;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class GeoRefApiAdapter implements NormalizadorUbicacionAdapter{

  String url = "https://apis.datos.gob.ar/georef/api/ubicacion";


  WebClient webClient = WebClient.builder().baseUrl(url).build();

  @Override
  public Lugar obtenerLugar(Ubicacion ubicacion) {
        LugarDTO ubi = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .queryParam("lat", ubicacion.getLatitud())
            .queryParam("lon", ubicacion.getLongitud())
            .build())
            .retrieve()
            .bodyToMono(LugarDTO.class)
            .block();
        //convertir dto a lugar/
    Lugar lugar = new Lugar();
    lugar.setDepartamento(ubi.getUbicacion().getDepartamento().getNombre());
    lugar.setProvincia(ubi.getUbicacion().getProvincia().getNombre());
    lugar.setMunicipio(ubi.getUbicacion().getMunicipio().getNombre());
    return lugar;
  }
}
