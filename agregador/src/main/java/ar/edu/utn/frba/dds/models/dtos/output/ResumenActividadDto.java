package ar.edu.utn.frba.dds.models.dtos.output;

import lombok.Data;

@Data
public class ResumenActividadDto {
  private Long solicitudesEliminacion;
  private Long hechostotales;
  private Long fuentesTotales;
}
