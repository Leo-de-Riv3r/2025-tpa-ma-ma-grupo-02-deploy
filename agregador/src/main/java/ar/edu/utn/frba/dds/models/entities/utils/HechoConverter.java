package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.externalApi.GeoRefApiAdapter;
import ar.edu.utn.frba.dds.externalApi.NormalizadorUbicacionAdapter;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import org.springframework.stereotype.Component;

@Component
public class HechoConverter {
  static NormalizadorUbicacionAdapter normalizadorLugar = new GeoRefApiAdapter();
  public static Hecho fromDTO(HechoDTOEntrada dto, TipoFuente tipoFuente) {

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
    Lugar lugar = normalizadorLugar.obtenerLugar(hecho.getUbicacion());
    Ubicacion nuevaUbi = hecho.getUbicacion();
    nuevaUbi.setLugar(lugar);
    hecho.setUbicacion(nuevaUbi);
    //aplico normalizacion de categoria
    return hecho;
  }
}
