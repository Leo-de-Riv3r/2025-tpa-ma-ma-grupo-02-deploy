package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.services.SolicitudService;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
    return respuesta;
  }
}
