package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.stereotype.Service;

@Service
public class SolicitudService {
  private ISolicitudRepository hechosSolicitudesRepository;
  private final SpamService detectorSpam;

  public SolicitudService(ISolicitudRepository hechosSolicitudesRepository, SpamService detectorSpam) {
    this.hechosSolicitudesRepository = hechosSolicitudesRepository;
    this.detectorSpam = detectorSpam;
  }

  public void createSolicitud(Solicitud solicitud) {
    hechosSolicitudesRepository.save(solicitud);
    if (detectorSpam.esSpam(solicitud.getTexto()) || !solicitud.estaFundado()) {
      this.rechazarSolicitud(solicitud.getId(), "automatico");
    }
  }

  public void rechazarSolicitud(String id, String supervisor) {
    Solicitud solicitud = this.getSolicitud(id);
    solicitud.rechazar(supervisor);
    hechosSolicitudesRepository.save(solicitud);
  }

  public void aceptarSolicitud(String id, String supervisor) {
    Solicitud solicitud = this.getSolicitud(id);
    solicitud.aceptar(supervisor);
    hechosSolicitudesRepository.save(solicitud);
  }

  public Boolean hechoEliminado(Hecho hecho) {
    List <Solicitud> solicitudes = this.getSolicitudes();
    return solicitudes.stream().anyMatch(solicitud -> EqualsBuilder.reflectionEquals(solicitud.getTituloHecho(), hecho.getTitulo()));
  }

  public Solicitud getSolicitud(String solicitudId) {
    return hechosSolicitudesRepository.findById(solicitudId).orElseThrow(() -> new EntityNotFoundException("Solicitud con id " + solicitudId + " no encontrado"));
  }
  public List<Solicitud> getSolicitudes() {
    return hechosSolicitudesRepository.findAll();
  }
}