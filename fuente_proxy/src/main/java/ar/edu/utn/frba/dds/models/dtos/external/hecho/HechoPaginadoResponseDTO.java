package ar.edu.utn.frba.dds.models.dtos.external.hecho;

import java.util.List;
import lombok.Data;

@Data
public class HechoPaginadoResponseDTO {
  private int current_page;
  private List<HechoDTO> data;
  private String first_page_url;
  private int from;
  private int last_page;
  private String last_page_url;
  private String next_page_url;
  private String path;
  private int per_page;
  private String prev_page_url;
  private int to;
  private int total;
}
