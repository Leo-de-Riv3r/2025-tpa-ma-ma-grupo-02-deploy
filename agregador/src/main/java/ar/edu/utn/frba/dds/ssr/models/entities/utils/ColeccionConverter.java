package ar.edu.utn.frba.dds.ssr.models.entities.utils;

import ar.edu.utn.frba.dds.ssr.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.CriterioDtoSalida;
import ar.edu.utn.frba.dds.ssr.models.entities.Coleccion;
import ar.edu.utn.frba.dds.ssr.models.entities.Fuente;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ColeccionConverter {
  private final FuenteConverter fuenteConverter;
  public ColeccionConverter(FuenteConverter fuenteConverter) {
    this.fuenteConverter = fuenteConverter;
  }

  public  ColeccionDTOSalida fromEntity(Coleccion coleccion) {
    ColeccionDTOSalida respuesta = new ColeccionDTOSalida();
    respuesta.setId(coleccion.getId());
    respuesta.setTitulo(coleccion.getTitulo());
    respuesta.setDescripcion(coleccion.getDescripcion());
    Set<Fuente> fuentes = coleccion.getFuentes();
    respuesta.setFuentes(fuentes.stream().map(fuenteConverter::fromEntity).toList());

    if(!coleccion.getCriterios().isEmpty()) {
      List<CriterioDtoSalida> criterioDtoList = new ArrayList<>();
      coleccion.getCriterios().forEach(criterio -> {
        CriterioDtoSalida criterioDtoSalida = new CriterioDtoSalida(criterio.getTipoFiltro().toString());
        criterioDtoList.add(criterioDtoSalida);
      });
      respuesta.setCriterios(criterioDtoList);
    }
    if(coleccion.getAlgoritmoConsenso() != null) {
      respuesta.setAlgoritmoConsenso(  coleccion.getAlgoritmoConsenso().getClass().getSimpleName());
    }
    return respuesta;
  }
}
