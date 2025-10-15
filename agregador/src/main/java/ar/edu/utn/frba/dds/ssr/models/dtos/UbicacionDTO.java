package ar.edu.utn.frba.dds.ssr.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UbicacionDTO {
  @JsonProperty("departamento")
  private DepartamentoDTO departamento;
  @JsonProperty("provincia")
  private ProvinciaDTO provincia;
  @JsonProperty("municipio")
  private MunicipioDTO municipio;
}

