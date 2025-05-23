package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coleccion {
  private String handler;
  private String titulo;
  private String descripcion;
  private Set<FiltroStrategy> criterios;
  private Set<FuenteDeDatos> fuentes;

  public Coleccion(String titulo, String descripcion, Set<FiltroStrategy> criterios, Set<FuenteDeDatos> fuentes) {
    this.handler = UUID.randomUUID().toString().substring(0, 10);
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = criterios;
    this.fuentes = fuentes;
  }

  public Coleccion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = new HashSet<>();
    this.fuentes = new HashSet<>();
  }

  public Set<Hecho> obtenerHechos() {
    List<FuenteDeDatos> fuentes = getFuentes().stream().filter(fuente -> !fuente.tiempoReal()).toList();
    return fuentes.stream()
        .flatMap(fuente -> fuente.obtenerHechos(criterios).stream())
        .collect(Collectors.toSet());
  }

  public void eliminarCriterio(FiltroStrategy filtro) {
    this.criterios.remove(filtro);
  }

  public void agregarFuente(FuenteDeDatos fuente) {
    this.fuentes.add(fuente);
  }

}
