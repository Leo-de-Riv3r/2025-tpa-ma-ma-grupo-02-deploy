package ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.boot.model.naming.Identifier;

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

  protected Boolean cumpleConsensoBase(Hecho hecho, Set<Fuente> fuentes, Integer cantMin) {
    cantidadMinimaApariciones = cantMin;
    long apariciones = fuentes.stream()
        .map(fuente -> fuente.getHechos())
        .filter(hechos -> hechos.stream()
            .anyMatch(h -> h.getTitulo() == hecho.getTitulo() && h.getCategoria() == hecho.getCategoria() && h.getDescripcion() == hecho.getDescripcion() && h.getFechaAcontecimiento() == hecho.getFechaAcontecimiento()))
        .count();

    return apariciones >= cantMin;
  }

  public abstract Boolean cumpleConsenso(Hecho hecho, Set<Fuente> fuentes);

  public void actualizarHechos(Set<Hecho> hechos, Set<Fuente> fuentes) {
    this.hechosConsensuados.clear();
    Set<Hecho> hechosC = hechos.stream()
        .filter(h -> cumpleConsensoBase(h, fuentes, cantidadMinimaApariciones))
        .collect(Collectors.toSet());

    this.hechosConsensuados.addAll(hechosC);
  }

  public Set<Hecho> getHechosCurados() {
    return this.hechosConsensuados;
  }
}
