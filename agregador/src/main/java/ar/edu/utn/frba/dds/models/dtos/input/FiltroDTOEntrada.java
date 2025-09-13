package ar.edu.utn.frba.dds.models.dtos.input;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FiltroDTOEntrada {
  private String tipoFiltro;
  private String valor;              // para título o categoría
  private LocalDateTime fechaInicio; // para filtros de fecha
  private LocalDateTime fechaFin;
  private String provincia;
  private String municipio;
  private String departamento;
}
