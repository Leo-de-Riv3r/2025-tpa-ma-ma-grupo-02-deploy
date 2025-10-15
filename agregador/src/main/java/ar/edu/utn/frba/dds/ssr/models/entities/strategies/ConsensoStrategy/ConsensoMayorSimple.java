package ar.edu.utn.frba.dds.ssr.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.ssr.models.entities.Fuente;
import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Set;

@Entity
@DiscriminatorValue("mayoria_simple")
public class ConsensoMayorSimple extends IConsensoStrategy {
  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes) {
    Integer mitad = fuentes.size() / 2;
    return cumpleConsensoBase(hecho, fuentes, mitad);
  }
}

