package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "algoritmo_consenso")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class IConsensoStrategy {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column
  protected Integer cantidadMinimaApariciones = 0;

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(name = "hecho_consensuado", joinColumns = @JoinColumn(name = "algoritmo_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"))
  private Set<Hecho> hechosConsensuados = new HashSet<>();

  public abstract TipoAlgoritmo getTipo();

  // Nuevo método abstracto: solo calcula el número
  public abstract Integer calcularMinimoRequerido(int totalFuentes);

  public Set<Hecho> getHechosCurados() {
    return this.hechosConsensuados;
  }
}