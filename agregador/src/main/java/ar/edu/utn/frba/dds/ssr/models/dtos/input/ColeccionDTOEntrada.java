package ar.edu.utn.frba.dds.ssr.models.dtos.input;

import ar.edu.utn.frba.dds.ssr.models.dtos.FuenteDTO;
import java.util.Set;
import lombok.Data;

@Data
public class ColeccionDTOEntrada {
  private String titulo;
  private String descripcion;
  private Set<FuenteDTO> fuentes;
  private String algoritmo;
  private Set<FiltroDTOEntrada> filtros;
}
