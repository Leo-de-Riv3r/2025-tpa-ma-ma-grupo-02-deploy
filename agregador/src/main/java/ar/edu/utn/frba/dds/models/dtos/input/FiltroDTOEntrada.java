package ar.edu.utn.frba.dds.models.dtos.input;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FiltroDTOEntrada {
  private String tipoFiltro;
  private String valor;              // para título, categoria, provincia, municipio o departamento
  private LocalDate fechaInicio; // para filtros de fecha
  private LocalDate fechaFin;
}
