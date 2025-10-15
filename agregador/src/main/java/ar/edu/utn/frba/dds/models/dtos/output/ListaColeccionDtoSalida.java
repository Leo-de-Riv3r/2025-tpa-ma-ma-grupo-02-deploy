package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class ListaColeccionDtoSalida {
  private List<ColeccionDTOSalida> colecciones;
}
