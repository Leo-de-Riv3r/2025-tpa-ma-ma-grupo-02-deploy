package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.Formato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Multimedia {
  private String nombre;
  private String ruta;
  private Formato formato;
}
