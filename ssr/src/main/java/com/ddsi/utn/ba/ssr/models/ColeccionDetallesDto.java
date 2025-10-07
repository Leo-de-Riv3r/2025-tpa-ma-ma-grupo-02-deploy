package com.ddsi.utn.ba.ssr.models;

import java.util.List;
import lombok.Data;

@Data
public class ColeccionDetallesDto {
  private List<HechoDto> data;
  private Integer currentPage;
  private Integer perPage;
  private Integer totalPages;
}
