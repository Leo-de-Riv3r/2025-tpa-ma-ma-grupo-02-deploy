package ar.edu.utn.frba.dds.ssr.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.ssr.models.entities.Fuente;
import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Set;

@Entity
@DiscriminatorValue("absoluto")
public class ConsensoAbsoluto extends IConsensoStrategy {
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes) {
    return cumpleConsensoBase(hecho, fuentes, fuentes.size());
  }
}

