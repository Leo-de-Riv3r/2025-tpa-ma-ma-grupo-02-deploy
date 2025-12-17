package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("mayoria_simple")
public class ConsensoMayorSimple extends IConsensoStrategy {
  @Override
  public TipoAlgoritmo getTipo() {
    return TipoAlgoritmo.MAYORIA_SIMPLE;
  }

  @Override
  public Integer calcularMinimoRequerido(int totalFuentes) {
    return (int) Math.ceil(totalFuentes / 2.0);
  }
}