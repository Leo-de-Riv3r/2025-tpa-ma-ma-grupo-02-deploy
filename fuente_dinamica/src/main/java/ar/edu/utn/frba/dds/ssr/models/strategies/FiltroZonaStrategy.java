package ar.edu.utn.frba.dds.ssr.models.strategies;

import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import ar.edu.utn.frba.dds.ssr.models.entities.Zona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroZonaStrategy implements IFiltroStrategy {
  private Zona zona;

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return zona.pertenece(hecho.getUbicacion());
  }
}
