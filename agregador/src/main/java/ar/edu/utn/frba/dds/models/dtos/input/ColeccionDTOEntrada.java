package ar.edu.utn.frba.dds.models.dtos.input;

import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.Data;

@Data
public class ColeccionDTOEntrada {
  private String titulo;
  private String descripcion;
  private Set<FuenteDTO> fuentes;
  private String algoritmo;
  @JsonProperty("criterios")
  private Set<FiltroDTOEntrada> filtros;
}
