package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "algoritmo_consenso")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipo")
public abstract class IConsensoStrategy {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;
  @Column
  protected Integer cantidadMinimaApariciones = 0;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name="hecho_consensuado",
      joinColumns = @JoinColumn(name="algoritmo_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name="hecho_id", referencedColumnName = "id")
  )
  //@Transient
  private Set<Hecho> hechosConsensuados = new HashSet<>();

  protected Boolean cumpleConsensoBase(Hecho hecho, Set<Fuente> fuentes, Integer numfuentes) {
    Integer cantidadMinima = this.getCantMinima(numfuentes);
    long apariciones = fuentes.stream()
        .map(fuente -> fuente.getHechos())
        .filter(hechos -> hechos.stream()
            .anyMatch(h -> Objects.equals(h.getTitulo(), hecho.getTitulo()) && Objects.equals(h.getCategoria(), hecho.getCategoria()) && Objects.equals(h.getDescripcion(), hecho.getDescripcion())))
        .count();
    if (apariciones >= cantidadMinima) {
      System.out.println("cant apariciones: " + apariciones);
    }
    return apariciones >= cantidadMinima;
  }

  public abstract Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes);

  public void actualizarHechos(Set<Hecho> hechos, Set<Fuente> fuentes) {
    this.hechosConsensuados.clear();
    Set<Hecho> hechosC = hechos.stream()
        .filter(h -> cumpleConsensoBase(h, fuentes, fuentes.size()))
        .collect(Collectors.toSet());
    this.hechosConsensuados.addAll(hechosC);
  }

  public Set<Hecho> getHechosCurados() {
    return this.hechosConsensuados;
  }

  public abstract Integer getCantMinima(Integer numFuentes);
}
