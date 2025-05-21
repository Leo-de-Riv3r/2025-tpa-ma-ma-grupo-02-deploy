package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Repository;


@Repository
public class ColecionesRepository implements IColeccionesRepository{
  List<Coleccion> colecciones;

  public void createColeccion(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

  public void updateColeccion(String handler, Coleccion coleccionAActualizar){
    colecciones.removeIf(coleccion -> EqualsBuilder.reflectionEquals(coleccion, coleccionAActualizar));
    colecciones.add(coleccionAActualizar);
  }


  @Override
  public void cambiarFuentesColeccion(String handler, Set<FuenteDeDatos> fuentes) {
    colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getHandler(), handler))
        .findFirst()
        .ifPresent(coleccion -> {
          Set<FuenteDeDatos> fuentesColeccion = coleccion.getFuentes();
          for (FuenteDeDatos fuente : fuentes) {
            if (fuente.tiempoReal()) {
              fuentesColeccion.remove(fuente);
              fuentesColeccion.add(fuente);
            }
          }
        });
  }


  @Override
  public void agregarFuente(String handler, FuenteDeDatos fuente) {
    colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getHandler(), handler))
        .findFirst()
        .get()
        .agregarFuente(fuente);
  }

  @Override
  public void agregarHechoTiempoReal(String handler, FuenteDeDatos fuenteTiempoReal, Hecho hecho) {
    colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getHandler(), handler))
        .findFirst().get()
        .getFuentes().stream().filter(fuente -> EqualsBuilder.reflectionEquals(fuente, fuenteTiempoReal))
        .findFirst().get()
        .agregarHecho(hecho);
  }


  public List<Coleccion> getColecciones(){
    return colecciones;
  }
}
