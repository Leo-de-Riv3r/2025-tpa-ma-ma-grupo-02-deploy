package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Origen {
  private TipoFuente tipo;
  private String nombreAutor;
  private String apellidoAutor;
  private Integer edadAutor;

  public Origen(TipoFuente tipo, String nombreAutor) {
    this.tipo = tipo;
    this.nombreAutor = nombreAutor;
  }
}
