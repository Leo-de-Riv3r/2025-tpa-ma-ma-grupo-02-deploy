package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Origen {
  private TipoOrigen tipo;
  private String nombreAutor;
  private String apellidoAutor;
  private Integer edadAutor;

  public Origen(TipoOrigen tipo, String nombreAutor) {
    this.tipo = tipo;
    this.nombreAutor = nombreAutor;
  }
}
