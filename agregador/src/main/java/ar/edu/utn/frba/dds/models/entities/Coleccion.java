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

@Getter
@Setter
public class Coleccion {
  private String id;
  private String titulo;
  private String descripcion;
  private Set<IFiltroStrategy> criterios;
  private Set<Fuente> fuentes;
  private IConsensoStrategy algoritmoConsenso;
  private Set<Hecho> hechosCurados;

  public Coleccion(String titulo, String descripcion) {
    this.id = UUID.randomUUID().toString();
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.criterios = Set.of();
    this.fuentes = Set.of();
    this.algoritmoConsenso = null;
    this.hechosCurados = Set.of();
  }

  public Set<Hecho> getHechos() {
    Set<Hecho> hechos = new HashSet<>();
    fuentes.forEach(fuente -> hechos.addAll(fuente.getHechos(criterios)));
    return hechos;
  }

  public void refrescarHechosCurados() {
    Set<Hecho> hechos = getHechos();
    if (algoritmoConsenso != null) {
      hechosCurados = hechos.stream().filter(hecho -> algoritmoConsenso.cumpleConsenso(hecho, fuentes, criterios)).collect(Collectors.toSet());
    } else {
      hechosCurados = hechos;
    }
  }

  public Set<Hecho> getHechosCurados() {
    if (hechosCurados.isEmpty()) {
      refrescarHechosCurados();
    }
    return hechosCurados;
  }

  public void addCriterio(IFiltroStrategy criterio) {
    this.criterios.add(criterio);
  }

  public void removeCriterio(IFiltroStrategy criterio) {
    this.criterios.remove(criterio);
  }

  public void addFuente(Fuente fuente) {
    this.fuentes.add(fuente);
  }

  public void removeFuente(String idFuente) {
    fuentes.removeIf(fuente -> EqualsBuilder.reflectionEquals(fuente.getId(), idFuente));
  }
}
