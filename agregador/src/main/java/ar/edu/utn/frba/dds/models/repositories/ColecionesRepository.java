package ar.edu.utn.frba.dds.models.repositories;

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


  public List<Coleccion> getColecciones(){
    return colecciones;
  }
}
