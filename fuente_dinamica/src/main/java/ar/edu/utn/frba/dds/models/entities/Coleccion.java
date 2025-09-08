package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.strategies.IFiltroStrategy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name = "coleccion")
@AllArgsConstructor
@NoArgsConstructor
public class Coleccion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "coleccion_titulo", nullable = false)
  private String titulo;

  @Column(name = "coleccion_descripcion")
  private String descripcion;

  @Transient
  private Set<IFiltroStrategy> criterios;

  @ManyToMany
  @JoinTable(
      name = "coleccion_fuente",
      joinColumns = @JoinColumn(name = "coleccion_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id")
  )
  private Set<FuenteDeDatos> fuentes;

  public Coleccion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = new HashSet<>();
    this.fuentes = new HashSet<>();
  }

  /*

  public boolean agregarHechos(Set<Hecho> hechos) {
    boolean alMenosUnoAgregado = false;
    for (Hecho hecho : hechos) {
      if (hechos.add(hecho)) {
        alMenosUnoAgregado = true;
      }
    }
    return alMenosUnoAgregado;
  }

  public boolean eliminarHechos(List<String> titulos) {
    boolean alMenosUnoEliminado = false;
    for (Hecho hecho : hechos) {
      if (titulos.contains(hecho.getTitulo()) && !hecho.getEliminado()) {
        hecho.setEliminado(Boolean.TRUE);
        alMenosUnoEliminado = true;
      }
    }
    return alMenosUnoEliminado;
  }

  */

  public Set<Hecho> obtenerHechos() {
    return fuentes.stream()
        .flatMap(fuente -> fuente.obtenerHechos(criterios).stream())
        .filter(hecho -> !hecho.getEliminado())
        .collect(Collectors.toSet());
  }

  public void agregarCriterio(IFiltroStrategy filtro) {
    this.criterios.add(filtro);
  }

  public void eliminarCriterio(IFiltroStrategy filtro) {
    this.criterios.remove(filtro);
  }

  public void agregarFuente(FuenteDeDatos fuente) {
    this.fuentes.add(fuente);
  }

}