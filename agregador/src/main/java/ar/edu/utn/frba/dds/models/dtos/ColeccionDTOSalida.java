package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.dtos.output.FiltroDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
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
  private List<FiltroDtoSalida> criterios;
  private String algoritmoConsenso;
  private String estado;
  public ColeccionDTOSalida() {
    this.cantSolicitudesSpam = 0;
  }
}
