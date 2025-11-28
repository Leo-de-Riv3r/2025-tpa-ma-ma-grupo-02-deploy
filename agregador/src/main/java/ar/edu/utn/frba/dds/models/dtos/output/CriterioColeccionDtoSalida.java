package ar.edu.utn.frba.dds.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CriterioColeccionDtoSalida {
  private String tipoFiltro;
  private String valor;         // para título, categoria, provincia, municipio o departamento
  private String tipoFuente; // para filtro de fuente
  private LocalDate fechaInicio; // para filtros de fecha
  private LocalDate fechaFin;
}
