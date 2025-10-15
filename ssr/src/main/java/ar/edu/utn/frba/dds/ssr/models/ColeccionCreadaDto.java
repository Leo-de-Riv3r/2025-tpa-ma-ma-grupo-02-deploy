package ar.edu.utn.frba.dds.ssr.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColeccionCreadaDto {
  @JsonProperty("id")
  private String id;
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("descripcion")
  private String descripcion;
}
