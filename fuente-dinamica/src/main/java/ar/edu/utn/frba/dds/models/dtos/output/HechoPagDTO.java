package ar.edu.utn.frba.dds.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class HechoPagDTO {
  @JsonProperty("current page")
  private Integer currentPage;
  private List<HechoOutputDTO> data;
  @JsonProperty("last_page")
  private Integer lastPage;

  public HechoPagDTO(Integer currentPage, List<HechoOutputDTO> data) {
    this.currentPage = currentPage;
    this.data = data;
  }
}
