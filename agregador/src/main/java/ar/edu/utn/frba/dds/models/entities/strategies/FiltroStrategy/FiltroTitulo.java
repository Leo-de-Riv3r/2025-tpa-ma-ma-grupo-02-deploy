package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroTitulo implements IFiltroStrategy {
  private String titulo;

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return hecho.getTitulo().equalsIgnoreCase(titulo);
  }
}
