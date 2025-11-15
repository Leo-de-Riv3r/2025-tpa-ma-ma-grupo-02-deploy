package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.dtos.output.CriterioDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColeccionDTOSalida {
  private String id;
  private String titulo;
  private String descripcion;
  private List<FuenteDTOOutput> fuentes;
  private Integer cantSolicitudesSpam;
  private List<String> criterios = List.of();
  private String algoritmoConsenso;
  public ColeccionDTOSalida() {
    this.cantSolicitudesSpam = 0;
  }
}
