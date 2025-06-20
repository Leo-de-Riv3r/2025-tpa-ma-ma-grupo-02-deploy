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
  private Set<IFuenteAbstract> fuentes;
  private AlgoritmoConsenso algoritmoConsenso;
  private List<Hecho> hechosConsensuados;
  public Coleccion(String titulo, String descripcion, Set<FiltroStrategy> criterios, Set<IFuenteAbstract> fuentes) {
    this.id = UUID.randomUUID().toString().substring(0, 10);
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = criterios;
    this.fuentes = fuentes;
    this.algoritmoConsenso = new AlgoritmoMayoriaAbsoluta();
  }

  public Coleccion(String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = new HashSet<>();
    this.fuentes = new HashSet<>();
  }

  public Set<Hecho> obtenerHechos() {
    Set<Hecho> hechos = new HashSet<>(Set.of());
    fuentes.forEach(fuente -> hechos.addAll(fuente.obtenerHechos(criterios)));
    return hechos;

  }

  public Set<Hecho> obtenerHechos(Integer page, Integer per_page) {
    Set<Hecho> hechos = new HashSet<>(Set.of());

    fuentes.forEach(fuente -> {
      hechos.addAll(fuente.obtenerHechosUrl(page, per_page).getHechosDTOEntrada().stream().map(fuente::convertirHechoDTOAHecho).toList());
    });
    return hechos;
  }
  public void eliminarCriterio(FiltroStrategy filtro) {
    this.criterios.remove(filtro);
  }

  public void agregarFuente(IFuenteAbstract fuente) {
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

  public void actualizarFuentes() {
    fuentes.forEach(IFuenteAbstract::actualizarHechos);
  }
}
