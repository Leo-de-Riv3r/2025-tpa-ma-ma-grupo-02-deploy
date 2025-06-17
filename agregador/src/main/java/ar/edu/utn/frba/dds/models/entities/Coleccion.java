package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;

@Getter
@Setter
public class Coleccion {
  private String id;
  private String titulo;
  private String descripcion;
  private Set<FiltroStrategy> criterios;
  private Set<IFuenteAdapter> fuentes;
  private AlgoritmoConsenso algoritmoConsenso;
  private List<Hecho> hechosConsensuados;
  public Coleccion(String titulo, String descripcion, Set<FiltroStrategy> criterios, Set<IFuenteAdapter> fuentes) {
    this.id = UUID.randomUUID().toString().substring(0, 10);
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = criterios;
    this.fuentes = fuentes;
    this.algoritmoConsenso = new AlgoritmoAbsoluto();
  }

  public Coleccion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = new HashSet<>();
    this.fuentes = new HashSet<>();
  }

  public Set<Hecho> obtenerHechos() {
    List<IFuenteAdapter> fuentes = getFuentes().stream().filter(fuente -> !fuente.tiempoReal()).toList();
    return fuentes.stream()
        .flatMap(fuente -> fuente.obtenerHechos(criterios).stream())
        .collect(Collectors.toSet());
  }

  public void eliminarCriterio(FiltroStrategy filtro) {
    this.criterios.remove(filtro);
  }

  public void agregarFuente(IFuenteAdapter fuente) {
    this.fuentes.add(fuente);
  }
  public void eliminarFuente(String idFuente) {
    fuentes.removeIf(fuente -> EqualsBuilder.reflectionEquals(fuente.getId(), idFuente));
  }

  public void setAlgoConsenso(AlgoritmoConsenso algoritmoConsenso) {
    this.algoritmoConsenso = algoritmoConsenso;
  }

  public void actualizarHechosConsensuados() {
      this.algoritmoConsenso.obtenerHechosConsensuados(fuentes, criterios);
    }
}
