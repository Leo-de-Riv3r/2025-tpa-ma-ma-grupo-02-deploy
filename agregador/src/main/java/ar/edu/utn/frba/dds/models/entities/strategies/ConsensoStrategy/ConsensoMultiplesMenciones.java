package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("multiples_menciones")
public class ConsensoMultiplesMenciones extends IConsensoStrategy {
  @Override
  public TipoAlgoritmo getTipo() {
    return TipoAlgoritmo.MULTIPLES_MENCIONES;
  }

  @Override
  public Integer calcularMinimoRequerido(int totalFuentes) {
    return 2;
  }
}