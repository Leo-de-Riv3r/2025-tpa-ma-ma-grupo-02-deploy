package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class SolicitudesPaginasDto {
  private Integer currentPage;
  private Integer totalPages;
  private List<SolicitudResumenDto> data;
}
