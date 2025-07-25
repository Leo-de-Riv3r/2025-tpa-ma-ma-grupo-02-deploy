package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroCategoria implements IFiltroStrategy {
  private String nombreCategoria;

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return hecho.getCategoria().equals(nombreCategoria);
  }
}
