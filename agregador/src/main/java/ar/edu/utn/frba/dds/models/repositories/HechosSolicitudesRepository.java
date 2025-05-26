package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class HechosSolicitudesRepository implements IHechosSolicitudesRepository{
  private List<Solicitud> solicitudes = List.of();
  private List<Hecho> hechos = List.of();
  @Override
  public void createSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
  }

  @Override
  public void aceptarSolicitud(Solicitud solicitud, String supervisor) {
      this.buscarSolicitud(solicitud).get().aceptar(supervisor);
  }

  @Override
  public void rechazarSolicitud(Solicitud solicitud, String supervisor) {
    this.buscarSolicitud(solicitud).get().rechazar(supervisor);
  }

  public boolean hechoEliminado(Hecho hecho) {
    return solicitudes.stream().anyMatch(solicitud -> EqualsBuilder.reflectionEquals(solicitud.getHecho(), hecho));
  }
  private Optional<Solicitud> buscarSolicitud(Solicitud solicitudBuscada) {
    return solicitudes.stream().filter(solicitud -> EqualsBuilder.reflectionEquals(solicitudBuscada, solicitud))
        .findFirst();
  }
}
