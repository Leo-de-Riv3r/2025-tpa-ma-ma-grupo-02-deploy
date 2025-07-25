package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FiltroDTOEntrada {
  private TipoFiltro tipoFiltro;
  private String valor;              // para título o categoría
  private LocalDateTime fechaInicio; // para filtros de fecha
  private LocalDateTime fechaFin;
  private Double latitud;           // para ubicación
  private Double longitud;
}
