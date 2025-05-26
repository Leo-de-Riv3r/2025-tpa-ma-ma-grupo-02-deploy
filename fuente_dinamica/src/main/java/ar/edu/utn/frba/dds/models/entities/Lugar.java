package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.TipoLugar;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lugar {
  private String nombre;
  private TipoLugar tipo;
}