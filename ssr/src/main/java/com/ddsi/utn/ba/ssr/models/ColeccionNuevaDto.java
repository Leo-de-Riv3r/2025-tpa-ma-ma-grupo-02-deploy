package com.ddsi.utn.ba.ssr.models;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class ColeccionNuevaDto {
  private String titulo;
  private String descripcion;
  private List<FuenteNuevaDto> fuentes;
  private String algoritmo;
  //private List<FiltroDTOEntrada> filtros;
}
