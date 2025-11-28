package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import java.util.stream.Collectors;
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

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "coleccion_id", referencedColumnName = "id")
  private Set<IFiltroStrategy> criterios = new HashSet<>();

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(
  name="hecho_filtrado",joinColumns = @JoinColumn(name = "coleccion_id", referencedColumnName = "id"),
  inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"))
  private Set<Hecho> hechosFiltrados = new HashSet<>();

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(name = "coleccion_fuente",
      joinColumns = @JoinColumn(name = "coleccion_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id")
  )
  private Set<Fuente> fuentes;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "algoritmo_id", referencedColumnName = "id")
  private IConsensoStrategy algoritmoConsenso;

  public Coleccion() {
    this.id = UUID.randomUUID().toString();
    this.fuentes = new HashSet<>();
  }

  public Set<Hecho> getHechos() {
    Set<Hecho> hechos = new HashSet<>();

    fuentes.stream()
        .forEach(fuente -> {
          hechos.addAll(fuente.getHechos());
          System.out.println("hechos de fuente: " + fuente.getHechos().size());
        });
    System.out.println("hechos de fuentes: " + hechos.size());
    //filtro duplicados segun titulo categoria descripcion y fecha acontecimiento
    if (!criterios.isEmpty() || criterios != null) {
      return hechos.stream().filter(h -> h.cumpleFiltros(criterios)).collect(Collectors.toSet());
    } else {
      return hechos;
    }
  }

  public void refrescarHechosCurados() {
    if (algoritmoConsenso != null) {

      algoritmoConsenso.actualizarHechos(this.getHechos(), fuentes);
    }
  }
  public Set<Hecho> getHechosFiltrados() {
    if (!criterios.isEmpty()){
      return this.getHechos().stream().filter(h -> h.cumpleFiltros(criterios)).collect(Collectors.toSet());
    }
    else return new HashSet<>();
  }
  public Set<Hecho> getHechosCurados() {
    if (algoritmoConsenso != null) {
      return algoritmoConsenso.getHechosCurados();
    } else {
      return new HashSet<>();
    }
  }

  public void addCriterio(IFiltroStrategy filtro) {
    this.criterios.add(filtro);
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

  public void actualizarHechosFiltrados() {
    this.hechosFiltrados.clear();
    Set<Hecho> hechosColeccion = this.getHechos();
    this.hechosFiltrados.addAll(hechosColeccion.stream().filter(h -> h.cumpleFiltros(criterios)).collect(Collectors.toSet()));
  }

  public void setearCriterios(Set<IFiltroStrategy> filtros) {
    this.criterios.clear();
    this.criterios.addAll(filtros);
  }

  public void limpiarCriterios() {
    this.criterios.clear();
  }
}
