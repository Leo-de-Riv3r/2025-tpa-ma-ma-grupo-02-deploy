package ar.edu.utn.frba.dds.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltroDtoSalida {
  private String tipoFiltro;
  private String valor;
  private String tipoFuente;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaInicio;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaFin;
}
