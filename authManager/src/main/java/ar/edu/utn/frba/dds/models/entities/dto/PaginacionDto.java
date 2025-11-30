package ar.edu.utn.frba.dds.models.entities.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaginacionDto<T> {
  private List<T> data;
  private int currentPage;
  private int totalPages;
}