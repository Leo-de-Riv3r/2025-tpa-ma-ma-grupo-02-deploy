package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.LugarDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HechoConverter {


  public Hecho fromDTO(HechoDTOEntrada dto, TipoFuente tipoFuente) {
    Ubicacion ubicacion = new Ubicacion();
    ubicacion.setLatitud(dto.getLatitud());
    ubicacion.setLongitud(dto.getLongitud());
    Origen origenExistente = new Origen();
    origenExistente.setTipo(tipoFuente);
    origenExistente.setNombreAutor("---");
    origenExistente.setApellidoAutor("---");
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
    return lugar;
  }

  public HechoDtoSalida fromEntity(Hecho hecho) {
    HechoDtoSalida hechoDtoSalida = new HechoDtoSalida();
    hechoDtoSalida.setTitulo(hecho.getTitulo());
    hechoDtoSalida.setDepartamento(hecho.getUbicacion().getLugar().getDepartamento());
    hechoDtoSalida.setMunicipio(hecho.getUbicacion().getLugar().getMunicipio());
    hechoDtoSalida.setProvincia(hecho.getUbicacion().getLugar().getProvincia());
    hechoDtoSalida.setCategoria(hecho.getCategoria());
    hechoDtoSalida.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoDtoSalida.setFechaCarga(hecho.getFechaCarga());
    return hechoDtoSalida;
  }

  public HechoDetallesDtoSalida fromEntityDetails(Hecho hecho) {
    //
    HechoDetallesDtoSalida hechoDetallesDtoSalida = new HechoDetallesDtoSalida();
    hechoDetallesDtoSalida.setId(hecho.getId());
    hechoDetallesDtoSalida.setDepartamento(hecho.getUbicacion().getLugar().getDepartamento());
    hechoDetallesDtoSalida.setProvincia(hecho.getUbicacion().getLugar().getProvincia());
    hechoDetallesDtoSalida.setMunicipio(hecho.getUbicacion().getLugar().getMunicipio());
    hechoDetallesDtoSalida.setLatitud(hecho.getUbicacion().getLatitud());
    hechoDetallesDtoSalida.setLongitud(hecho.getUbicacion().getLongitud());
    hechoDetallesDtoSalida.setCategoria(hecho.getCategoria());
    hechoDetallesDtoSalida.setFechaCarga(hecho.getFechaCarga());
    hechoDetallesDtoSalida.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoDetallesDtoSalida.setDescripcion(hecho.getDescripcion());
    hechoDetallesDtoSalida.setMultimedia(hecho.getMultimedia());
    hechoDetallesDtoSalida.setTitulo(hecho.getTitulo());
    hechoDetallesDtoSalida.setTipoOrigen(hecho.getOrigen().getTipo());
    return hechoDetallesDtoSalida;
  }
}
