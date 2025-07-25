package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.impl.SolicitudRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SolicitudService {
  private ISolicitudRepository hechosSolicitudesRepository = new SolicitudRepository();
  private final SpamService detectorSpam;

  public SolicitudService(ISolicitudRepository hechosSolicitudesRepository, SpamService detectorSpam) {
    this.hechosSolicitudesRepository = hechosSolicitudesRepository;
    this.detectorSpam = detectorSpam;
  }

  public void createSolicitud(Solicitud solicitud) {
    hechosSolicitudesRepository.createSolicitud(solicitud);
    if (detectorSpam.esSpam(solicitud.getTexto()) || !solicitud.estaFundado()) {
      this.rechazarSolicitud(solicitud.getId(), "automatico");
    }
  }

  public void rechazarSolicitud(String id, String supervisor) {
    hechosSolicitudesRepository.rechazarSolicitud(id, supervisor);
  }

  public void aceptarSolicitud(String id, String supervisor) {
    hechosSolicitudesRepository.aceptarSolicitud(id, supervisor);
  }

  public Boolean hechoEliminado(Hecho hecho) {
    return hechosSolicitudesRepository.hechoEliminado(hecho);
  }

  public List<Solicitud> getSolicitudes() {
    return hechosSolicitudesRepository.getSolicitudes();
  }
}