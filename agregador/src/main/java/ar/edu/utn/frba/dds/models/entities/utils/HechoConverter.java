package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.externalApi.GeoRefApiAdapter;
import ar.edu.utn.frba.dds.externalApi.NormalizadorUbicacionAdapter;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.LugarDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class HechoConverter {


  public Hecho fromDTO(HechoDTOEntrada dto, TipoFuente tipoFuente) {
    Ubicacion ubicacion = new Ubicacion();
    ubicacion.setLatitud(dto.getLatitud());
    ubicacion.setLongitud(dto.getLongitud());
    Origen origenExistente = new Origen();
    origenExistente.setTipo(tipoFuente);
    Hecho hecho = new Hecho();
    hecho.setTitulo(dto.getTitulo());
    hecho.setDescripcion(dto.getDescripcion());
    hecho.setCategoria(dto.getCategoria());
    hecho.setUbicacion(ubicacion);
    hecho.setFechaAcontecimiento(dto.getFechaHecho());
    hecho.setFechaCarga(dto.getCreatedAt());
    hecho.setOrigen(origenExistente);
    //aplico normalizacion de categoria
    return hecho;
  }

  public Lugar obtenerLugar(Ubicacion ubicacion) {
    String url = "https://apis.datos.gob.ar/georef/api/ubicacion";
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    System.out.println("latitud: " + ubicacion.getLatitud());
    System.out.println("longitud: " + ubicacion.getLongitud());
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

    assert ubi != null;
    lugar.setDepartamento(ubi.getUbicacion().getDepartamento().getNombre());
    lugar.setProvincia(ubi.getUbicacion().getProvincia().getNombre());
    lugar.setMunicipio(ubi.getUbicacion().getMunicipio().getNombre());
    System.out.println(lugar);
    return lugar;
  }
}
