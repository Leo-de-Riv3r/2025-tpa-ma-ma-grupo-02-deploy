package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.IFuenteAbstract;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Repository;


@Repository
public class ColecionesRepository implements IColeccionesRepository{
  List<Coleccion> colecciones = List.of();

  @Override
  public void createColeccion(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

  @Override
  public void updateColeccion(String handler, Coleccion coleccionAActualizar){
    colecciones.removeIf(coleccion -> EqualsBuilder.reflectionEquals(coleccion, coleccionAActualizar));
    coleccionAActualizar.setId(handler);
    colecciones.add(coleccionAActualizar);
  }

  @Override
  public Optional<Coleccion> findById(String handler) {
    return colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getId(), handler))
        .findFirst();
  }

  @Override
  public void agregarFuente(String handler, IFuenteAbstract fuente) {
    colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getId(), handler))
        .findFirst()
        .ifPresent(coleccion -> coleccion.agregarFuente(fuente));
  }

  @Override
  public List<Coleccion> getColecciones(){
    return colecciones;
  }

  @Override
  public Coleccion deleteColeccion(String id) {
    Optional<Coleccion> coleccionAEliminar = colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getId(), id))
        .findFirst();
    if (coleccionAEliminar.isPresent()) {
        colecciones.remove(coleccionAEliminar.get());
        return coleccionAEliminar.get();
    } else {
      return null;
    }
  }

  @Override
  public void eliminarFuente(String id, String idFuente) {
    findById(id).ifPresent(coleccion -> coleccion.eliminarFuente(idFuente));
  }

  @Override
  public void setAlgoritmoConsenso(String id, AlgoritmoConsenso algoritmoConsenso) {
    findById(id).ifPresent(coleccion -> coleccion.setAlgoritmoConsenso(algoritmoConsenso));
  }

  @Override
  public void actualizarFuentes() {
    colecciones.forEach(Coleccion::actualizarFuentes);
  }

  @Override
  public void actualizarHechosConsensuados() {
    colecciones.forEach(Coleccion::actualizarHechosConsensuados);
  }
}
