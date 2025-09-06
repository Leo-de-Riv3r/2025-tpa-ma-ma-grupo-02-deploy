package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import jakarta.persistence.*;

@Getter
@Setter
@Entity @Table(name = "coleccion")
public class Coleccion {
  @Id
  private String id;
  @Column
  private String titulo;
  @Column
  private String descripcion;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name ="coleccion_fuente",
      joinColumns = @JoinColumn(name = "coleccion_id",
      referencedColumnName = "id"),
      inverseJoinColumns =  @JoinColumn(name = "fuente_id",
      referencedColumnName = "id")
  )
  private Set<Fuente> fuentes;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "algoritmo_id", referencedColumnName = "id")
  private IConsensoStrategy algoritmoConsenso;

  public Coleccion() {
    this.id = UUID.randomUUID().toString();
    this.fuentes = new HashSet<>();
  }

  public Set<Hecho> getHechos() {
    Set<Hecho> hechos = new HashSet<>();
    fuentes.stream().filter(f -> f.getInactivo() != 1)
        .forEach(fuente -> hechos.addAll(fuente.getHechos()));
    return hechos;
  }

  public void refrescarHechosCurados(EntityManager em) {
    if (algoritmoConsenso != null) {
      algoritmoConsenso.actualizarHechos(this.getHechos(), fuentes, em);
    }
  }

  public Set<Hecho> getHechosCurados() {
//    if (algoritmoConsenso !=null && algoritmoConsenso.getHechosCurados().isEmpty()) {
//      refrescarHechosCurados();
//      return algoritmoConsenso.getHechosCurados();
//    } else{
//      return this.getHechos();
//    }
    if (algoritmoConsenso != null) {
      return algoritmoConsenso.getHechosCurados();
    } else {
      return new HashSet<>();
    }
  }

  public void addFuente(Fuente fuente) {
    this.fuentes.add(fuente);
  }

  public void removeFuente(String idFuente) {
    fuentes.removeIf(fuente -> EqualsBuilder.reflectionEquals(fuente.getId(), idFuente));
  }

  public void limpiarFuentes() {
    this.fuentes.clear();
  }

  public void setearFuentes(Set<Fuente> fuentes) {
    this.fuentes.addAll(fuentes);
  }
}
