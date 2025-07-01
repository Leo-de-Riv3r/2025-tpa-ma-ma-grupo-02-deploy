package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudRepository;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class SolicitudRepository implements ISolicitudRepository {
  private List<Solicitud> solicitudes = List.of();

  @Override
  public void createSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
  }

  @Override
  public void aceptarSolicitud(String id, String supervisor) {
    this.buscarSolicitud(id).ifPresent(s -> s.aceptar(supervisor));
  }

  @Override
  public void rechazarSolicitud(String id, String supervisor) {
    this.buscarSolicitud(id).ifPresent(s -> s.rechazar(supervisor));
  }

  public boolean hechoEliminado(Hecho hecho) {
    return solicitudes.stream().anyMatch(solicitud -> EqualsBuilder.reflectionEquals(solicitud.getHecho(), hecho));
  }

  @Override
  public List<Solicitud> getSolicitudes() {
    return solicitudes;
  }

  private Optional<Solicitud> buscarSolicitud(String id) {
    return solicitudes.stream().filter(solicitud -> EqualsBuilder.reflectionEquals(id, solicitud.getId())).findFirst();
  }
}
