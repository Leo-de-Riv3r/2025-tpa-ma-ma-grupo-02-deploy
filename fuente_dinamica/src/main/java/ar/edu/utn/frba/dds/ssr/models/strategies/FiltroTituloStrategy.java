package ar.edu.utn.frba.dds.ssr.models.strategies;

import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroTituloStrategy implements IFiltroStrategy {
  private String titulo;

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return hecho.getTitulo().equalsIgnoreCase(titulo);
  }
}
