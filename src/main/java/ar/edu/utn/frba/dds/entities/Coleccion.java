package ar.edu.utn.frba.dds.entities;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coleccion {
  private String titulo;
  private String descripcion;
  private Set<Hecho> hechos;
  private Set<Filtro> criterios;

  public Coleccion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
  }

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
        hecho.setEliminado(true);
        alMenosUnoEliminado = true;
      }
    }
    return alMenosUnoEliminado;
  }

  public Set<Hecho> getHechos() {
    return hechos.stream().filter(hecho -> !hecho.getEliminado() && criterios.stream().allMatch(c -> c.cumpleFiltro(hecho))).collect(Collectors.toSet());
  }

  public void agregarCriterio(Filtro filtro) {
    this.criterios.add(filtro);
  }

  public void eliminarCriterio(Filtro filtro) {
    this.criterios.remove(filtro);
  }
}
