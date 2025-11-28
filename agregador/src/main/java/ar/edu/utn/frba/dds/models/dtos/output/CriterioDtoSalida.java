package ar.edu.utn.frba.dds.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class CriterioDtoSalida {
  private String tipoCriterio;
  private String valor;   // para título, categoria, provincia, municipio o departamento
  private String tipoFuente; // para filtro de fuente
  private LocalDate fechaInicio; // para filtros de fecha
  private LocalDate fechaFin;
}
