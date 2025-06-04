package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class SolicitudesRepository implements ISolicitudesRepository {
  private List<Solicitud> solicitudes = List.of();

  @Override
  public void createSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
  }

  @Override
  public void aceptarSolicitud(Solicitud solicitud, String supervisor) {
      this.buscarSolicitud(solicitud).ifPresent(s -> s.aceptar(supervisor));
  }

  @Override
  public void rechazarSolicitud(Solicitud solicitud, String supervisor) {
    this.buscarSolicitud(solicitud).ifPresent(s -> s.rechazar(supervisor));
  }

  public boolean hechoEliminado(Hecho hecho) {
    return solicitudes.stream().anyMatch(solicitud -> EqualsBuilder.reflectionEquals(solicitud.getHecho(), hecho));
  }

  @Override
  public List<Solicitud> getSolicitudes() {
    return solicitudes;
  }

  private Optional<Solicitud> buscarSolicitud(Solicitud solicitudBuscada) {
    return solicitudes.stream().filter(solicitud -> EqualsBuilder.reflectionEquals(solicitudBuscada, solicitud))
        .findFirst();
  }
}
