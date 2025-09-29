package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.LugarDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.MultimediaDtoOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.Formato;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import java.util.ArrayList;
import java.util.List;
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
    if (dto.getIdAutor() != null) {
      origenExistente.setIdAutor(dto.getIdAutor());
    }

    Hecho hecho = new Hecho();
    hecho.setTitulo(dto.getTitulo());
    hecho.setDescripcion(dto.getDescripcion());
    hecho.setCategoria(dto.getCategoria());
    hecho.setUbicacion(ubicacion);
    hecho.setFechaAcontecimiento(dto.getFechaHecho());
    hecho.setFechaCarga(dto.getCreatedAt());
    hecho.setOrigen(origenExistente);
    if (dto.getMultimedia() != null) {
      List<Multimedia> listaMultimedia = new ArrayList<>();
      dto.getMultimedia().forEach(multimediaDtoInput -> {
        Multimedia multimedia = new Multimedia();
        multimedia.setNombre(multimediaDtoInput.getNombre());
        multimedia.setRuta(multimediaDtoInput.getRuta());
        try {
          Formato formato = Formato.valueOf(multimediaDtoInput.getFormato().toUpperCase());
          multimedia.setFormato(formato);
        } catch (Exception e){
          throw new IllegalArgumentException("Tipo de formato " + multimediaDtoInput + " no soportado");
        }
      });

      hecho.setMultimedia(listaMultimedia);
    }

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
    hechoDtoSalida.setId(hecho.getId());
    hechoDtoSalida.setTitulo(hecho.getTitulo());
    if (hecho.getUbicacion().getLugar()!=null) {
      if (hecho.getUbicacion().getLugar().getDepartamento() != null) {
        hechoDtoSalida.setDepartamento(hecho.getUbicacion().getLugar().getDepartamento());
      }
      if (hecho.getUbicacion().getLugar().getMunicipio() != null) {
        hechoDtoSalida.setMunicipio(hecho.getUbicacion().getLugar().getMunicipio());
      }
      if (hecho.getUbicacion().getLugar().getProvincia() != null) {
        hechoDtoSalida.setProvincia(hecho.getUbicacion().getLugar().getProvincia());
      }
    }
    hechoDtoSalida.setCategoria(hecho.getCategoria());
    hechoDtoSalida.setLatitud(hecho.getUbicacion().getLatitud());
    hechoDtoSalida.setLongitud(hecho.getUbicacion().getLongitud());
    return hechoDtoSalida;
  }

  public HechoDetallesDtoSalida fromEntityDetails(Hecho hecho) {
    //
    HechoDetallesDtoSalida hechoDetallesDtoSalida = new HechoDetallesDtoSalida();
    hechoDetallesDtoSalida.setId(hecho.getId());

    if(hecho.getUbicacion().getLugar().getDepartamento() != null) {
      hechoDetallesDtoSalida.setDescripcion(hecho.getUbicacion().getLugar().getDepartamento());
    }
    if(hecho.getUbicacion().getLugar().getMunicipio() != null) {
      hechoDetallesDtoSalida.setMunicipio(hecho.getUbicacion().getLugar().getMunicipio());
    }
    if (hecho.getUbicacion().getLugar().getProvincia() != null) {
      hechoDetallesDtoSalida.setProvincia(hecho.getUbicacion().getLugar().getProvincia());
    }
    hechoDetallesDtoSalida.setLatitud(hecho.getUbicacion().getLatitud());
    hechoDetallesDtoSalida.setLongitud(hecho.getUbicacion().getLongitud());
    hechoDetallesDtoSalida.setCategoria(hecho.getCategoria());
    hechoDetallesDtoSalida.setFechaCarga(hecho.getFechaCarga());
    hechoDetallesDtoSalida.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoDetallesDtoSalida.setDescripcion(hecho.getDescripcion());
    hechoDetallesDtoSalida.setTitulo(hecho.getTitulo());
    hechoDetallesDtoSalida.setTipoOrigen(hecho.getOrigen().getTipo());

    if (hecho.getMultimedia() != null) {
      List<MultimediaDtoOutput> listaMultimedia = new ArrayList<>();
      hecho.getMultimedia().forEach(multimedia -> {
        MultimediaDtoOutput multimediaDtoOutput = new MultimediaDtoOutput();
        multimediaDtoOutput.setNombre(multimedia.getNombre());
        multimediaDtoOutput.setRuta(multimedia.getRuta());
        multimediaDtoOutput.setFormato(multimedia.getFormato().toString());
        listaMultimedia.add(multimediaDtoOutput);
      });
      hechoDetallesDtoSalida.setMultimedia(listaMultimedia);
    }
    return hechoDetallesDtoSalida;
  }
}
