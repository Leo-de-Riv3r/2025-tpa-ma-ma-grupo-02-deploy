package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import java.util.List;

public class HechosSolicitudesRepository implements IHechosSolicitudesRepository{
  private List<Solicitud> solicitudes;
  private List<Hecho> hechos;
  @Override
  public void createSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
  }

  @Override
  public void aceptarSolicitud() {

  }

  @Override
  public void rechazarSolicitud() {

  }
}
