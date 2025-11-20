package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class PaginacionDtoHechoDtoSalida {
  private Integer currentPage;
  private Integer totalPages;
  private List<HechoPaginacionDto> data;
}
