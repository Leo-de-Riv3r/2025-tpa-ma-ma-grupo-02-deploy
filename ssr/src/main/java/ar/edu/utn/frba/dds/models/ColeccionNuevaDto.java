package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;

@Data
public class ColeccionNuevaDto {
  private String titulo;
  private String descripcion;
  private List<FuenteNuevaDto> fuentes;
  private String algoritmo;
  //private List<FiltroDTOEntrada> filtros;
}
