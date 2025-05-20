package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ColecionesRepository implements IColeccionesRepository{
  //TODO cambiar import de coleccion
  List<Coleccion> colecciones;

  public void createColeccion(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

  public void updateColeccion(String handler, Coleccion coleccionAActualizar){
    colecciones.removeIf(coleccion -> coleccion.equals(coleccionAActualizar));
    colecciones.add(coleccionAActualizar);
  }

  public List<Coleccion> getColecciones(){
    return colecciones;
  }
}
