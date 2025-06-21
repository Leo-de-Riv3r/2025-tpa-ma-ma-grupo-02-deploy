package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.SolicitudesRepository;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudesRepository;
import ar.edu.utn.frba.dds.services.ISolicitudesService;
import ar.edu.utn.frba.dds.services.IDetectorSpam;
import java.util.List;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.builder.EqualsBuilder;

@Service
public class SolicitudesService implements ISolicitudesService {
  private ISolicitudesRepository hechosSolicitudesRepository = new SolicitudesRepository();
  private IDetectorSpam detectorSpam;

  public SolicitudesService(ISolicitudesRepository hechosSolicitudesRepository, IDetectorSpam detectorSpam, IColeccionesRepository coleccionesRepository) {
    this.hechosSolicitudesRepository = hechosSolicitudesRepository;
    this.detectorSpam = detectorSpam;
  }

  @Override
  public void createSolicitud(Solicitud solicitud) {
    hechosSolicitudesRepository.createSolicitud(solicitud);
    if (!detectorSpam.esSpam(solicitud.getTexto())) {
      this.rechazarSolicitud(solicitud.getId(), "automatico");
    }
  }

  @Override
  public void rechazarSolicitud(String id, String supervisor)  {
    hechosSolicitudesRepository.rechazarSolicitud(id, supervisor);
  }

  @Override
  public void aceptarSolicitud(String id, String supervisor) {
    hechosSolicitudesRepository.aceptarSolicitud(id, supervisor);
  }

  @Override
  public Boolean hechoEliminado(Hecho hecho) {
    return  this.getSolicitudes().stream().anyMatch(solicitud -> EqualsBuilder.reflectionEquals(solicitud.getHecho(), hecho) && solicitud.estaAceptada());
  }

  public List<Solicitud> getSolicitudes() {
    return hechosSolicitudesRepository.getSolicitudes();
  }
//
}