package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("absoluto")
public class ConsensoAbsoluto extends IConsensoStrategy {
  @Override
  public TipoAlgoritmo getTipo() {
    return TipoAlgoritmo.ABSOLUTO;
  }

  @Override
  public Integer calcularMinimoRequerido(int totalFuentes) {
    return totalFuentes;
  }
}