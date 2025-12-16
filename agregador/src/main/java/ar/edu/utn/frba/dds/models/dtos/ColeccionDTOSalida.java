package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.dtos.output.FiltroDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOSalida;
import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColeccionDTOSalida {
  private String id;
  private String titulo;
  private String descripcion;
  private List<FuenteDTOSalida> fuentes;
  private List<FiltroDTOSalida> criterios;
  private String algoritmoConsenso;
  private EstadoColeccion estado;
}
