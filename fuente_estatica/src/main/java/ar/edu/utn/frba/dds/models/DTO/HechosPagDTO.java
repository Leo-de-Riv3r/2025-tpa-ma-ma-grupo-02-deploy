package ar.edu.utn.frba.dds.models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class HechosPagDTO {
  @JsonProperty("currentPage")
  private Integer currentPage;
  private List<HechoDTO> data;
  private Integer lastPage;
  //private String first_page_url;
  //private Integer from;
//  @JsonProperty("last_page")
//  private Integer lastPage;
  //private String last_page_url;
  //private String next_page_url;
  //private String path;
  //private Integer per_page;
  //private String prev_page_url;
  //private Integer to;
  //private Integer total;
  public HechosPagDTO(Integer currentPage, List<HechoDTO> data, Integer lastPage) {
    this.currentPage = currentPage;
    this.data = data;
    this.lastPage = lastPage;
  }
}
