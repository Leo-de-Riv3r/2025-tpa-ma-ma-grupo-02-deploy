package ar.edu.utn.frba.dds.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class FuenteResponseDTO {
  private Integer currentPage;
  private List<Object> hechosDTOEntrada;
}
