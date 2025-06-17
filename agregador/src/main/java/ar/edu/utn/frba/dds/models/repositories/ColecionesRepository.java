package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.IFuenteAdapter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Repository;


@Repository
public class ColecionesRepository implements IColeccionesRepository{
  List<Coleccion> colecciones = List.of();

  public void createColeccion(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

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
  public void cambiarFuentesColeccion(String handler, Set<IFuenteAdapter> fuentes) {
    colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getId(), handler))
        .findFirst()
        .ifPresent(coleccion -> {
          Set<IFuenteAdapter> fuentesColeccion = coleccion.getFuentes();
          for (IFuenteAdapter fuente : fuentes) {
            if (!fuente.tiempoReal()) {
              fuentesColeccion.remove(fuente);
              fuentesColeccion.add(fuente);
            }
          }
        });
  }


  @Override
  public void agregarFuente(String handler, IFuenteAdapter fuente) {
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
  public void eliminarFuente(String idColeccion, String idFuente) {
    Coleccion coleccion = colecciones.stream().filter(c -> EqualsBuilder.reflectionEquals(c.getId(), idColeccion))
        .findFirst().orElse(null);
    coleccion.eliminarFuente(idFuente);
  }

  public void setAlgoritmoConsenso(String id, AlgoritmoConsenso algoritmoConsenso) {
    findById(id).ifPresent(coleccion -> coleccion.setAlgoritmoConsenso(algoritmoConsenso));
  }
}
