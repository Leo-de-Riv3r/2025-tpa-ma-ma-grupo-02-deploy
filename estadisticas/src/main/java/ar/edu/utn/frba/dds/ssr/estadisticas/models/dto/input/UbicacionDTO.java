package ar.edu.utn.frba.dds.ssr.estadisticas.models.dto.input;

import lombok.Data;

@Data
public class UbicacionDTO {
  private Double latitud;
  private  Double longitud;
  private LugarDTO lugar;
}
