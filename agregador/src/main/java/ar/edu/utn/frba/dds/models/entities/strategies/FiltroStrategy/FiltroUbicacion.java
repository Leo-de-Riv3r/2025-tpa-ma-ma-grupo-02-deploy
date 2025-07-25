package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroUbicacion implements IFiltroStrategy {
  private Double latitud;
  private Double longitud;

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return hecho.getUbicacion().mismaUbicacion(this.latitud, this.longitud);
  }
}
