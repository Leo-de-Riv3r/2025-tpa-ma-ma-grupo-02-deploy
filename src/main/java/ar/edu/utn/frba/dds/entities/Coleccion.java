package ar.edu.utn.frba.dds.entities;

import java.util.HashSet;
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
  private Set<FiltroStrategy> criterios;
  private Set<FuenteDeDatos> fuentes; // Esto no deberia ser un strategy entonces?

  public Coleccion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = new HashSet<>();
    this.fuentes = new HashSet<>();
  }

  // ------ Esto habia que sacarlo, no?

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

  // ------

  public Set<Hecho> obtenerHechos() {
    return fuentes.stream()
        .flatMap(fuente -> fuente.obtenerHechos().stream())
        .filter(hecho -> !hecho.getEliminado() && criterios.stream().allMatch(filtro -> filtro.cumpleFiltro(hecho)))
        .collect(Collectors.toSet());
  }

  public void agregarCriterio(FiltroStrategy filtro) {
    this.criterios.add(filtro);
  }

  public void eliminarCriterio(FiltroStrategy filtro) {
    this.criterios.remove(filtro);
  }
}
