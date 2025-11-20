package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class ColeccionHechosDto {
  private String id;
  private PaginacionDtoHechoDtoSalida hechos;
}
