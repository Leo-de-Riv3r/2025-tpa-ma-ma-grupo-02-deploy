package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class PaginacionDtoBlockedIp {
  private List<BlockedIpDtoInput> data;
  private int currentPage;
  private int totalPages;
}
