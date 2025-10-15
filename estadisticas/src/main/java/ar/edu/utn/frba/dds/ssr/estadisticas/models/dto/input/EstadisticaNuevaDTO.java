package ar.edu.utn.frba.dds.ssr.estadisticas.models.dto.input;

import jdk.jfr.DataAmount;
import lombok.Data;

@Data
public class EstadisticaNuevaDTO {
  private String urlColeccion;
  private String categoriaEspecifica;
}
