package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class HechosSolicitudesRepository implements IHechosSolicitudesRepository{
  private List<Solicitud> solicitudes;
  private List<Hecho> hechos;
  @Override
  public void createSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
  }

  @Override
  public void aceptarSolicitud(Solicitud solicitud, String supervisor) {
    if (!solicitudes.isEmpty()) {
      this.buscarSolicitud(solicitud).get().aceptar(supervisor);
    }
  }

  @Override
  public void rechazarSolicitud(Solicitud solicitud, String supervisor) {
    if (!solicitudes.isEmpty()) {
      this.buscarSolicitud(solicitud).get().rechazar(supervisor);
    }
  }

  private Optional<Solicitud> buscarSolicitud(Solicitud solicitudBuscada) {
    return solicitudes.stream().filter(solicitud -> solicitud.equals(solicitudBuscada))
        .findFirst();
  }
}
