package com.ddsi.utn.ba.ssr.models;

import java.util.List;
import lombok.Data;

@Data
public class SolicitudesPaginasDto {
  private List<SolicitudResumenDto> data;
  private Integer currentPage;
  private Integer totalPages;
}
