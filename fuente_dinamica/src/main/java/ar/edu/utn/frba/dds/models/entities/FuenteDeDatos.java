package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.strategies.IFiltroStrategy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity @Table(name = "fuente")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class FuenteDeDatos {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "fuente_id", referencedColumnName = "id")
  private List<Hecho> hechos = new ArrayList<>();

  public abstract List<Hecho> obtenerHechos(Set<IFiltroStrategy> criterios);
  public abstract void eliminarHecho(Hecho hecho);
}
