package ar.edu.utn.frba.dds.models.dtos;

import java.util.Set;
import lombok.Data;

@Data
public class ColeccionDTOEntrada {
  private String titulo;
  private String descripcion;
  //private Set<FiltroDTO> filtros;
  private Set<FuenteDTO> fuentes;
  private String algoritmo;
}
