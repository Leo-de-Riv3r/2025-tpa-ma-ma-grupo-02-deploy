package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import java.util.List;

public class ColecionesRepository implements IColeccionesRepository{
  //TODO cambiar import de coleccion
  List<Coleccion> colecciones;

  public void createColeccion(Coleccion coleccion) {
    colecciones.add(coleccion);
  }

  public void actualizarColecciones(){
    for(Coleccion coleccion : colecciones) {
      //TODO
      //en principio tiene que ser coleccion.gethechos
      //ver como hacer que consulten con la api de coleccion correspondiente
    }
  }
}
