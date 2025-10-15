package ar.edu.utn.frba.dds.ssr.services;

import ar.edu.utn.frba.dds.ssr.externalApi.SpamApi;
import ar.edu.utn.frba.dds.ssr.models.dtos.input.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.SolicitudDTOOutput;
import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import ar.edu.utn.frba.dds.ssr.models.entities.Solicitud;
import ar.edu.utn.frba.dds.ssr.models.entities.utils.SolicitudConverter;
import ar.edu.utn.frba.dds.ssr.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.ssr.models.repositories.ISolicitudRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

@Service
public class SolicitudService {
  private final ISolicitudRepository solicitudesEliminacionRepo;
  private final SpamApi detectorSpam;
  private final IHechoRepository hechosRepository;
  private final SolicitudConverter solicitudConverter;
  public SolicitudService(ISolicitudRepository solicitudesEliminacionRepo, SpamApi detectorSpam, IHechoRepository hechosRepository, SolicitudConverter solicitudConverter) {
    this.solicitudesEliminacionRepo = solicitudesEliminacionRepo;
    this.detectorSpam = detectorSpam;
    this.hechosRepository = hechosRepository;
    this.solicitudConverter = solicitudConverter;
  }

  @Transactional
  public void createSolicitud(SolicitudDTOEntrada dtoSolicitud) {
    Solicitud solicitud = solicitudConverter.fromDto(dtoSolicitud);
    Solicitud solicitudGuardada = solicitudesEliminacionRepo.save(solicitud);
    if (detectorSpam.esSpam(solicitud.getTexto()) || !solicitud.estaFundado()) {
      this.marcarComospam(solicitudGuardada.getId());
      this.rechazarSolicitud(solicitudGuardada.getId());
    }
  }

  private void marcarComospam(Long id) {
    Solicitud solicitud = this.getSolicitud(id);
    solicitud.marcarSpam();
    solicitudesEliminacionRepo.save(solicitud);
  }

  public void rechazarSolicitud(Long id) {
    Solicitud solicitud = this.getSolicitud(id);
    solicitud.rechazar();
    solicitudesEliminacionRepo.save(solicitud);
  }

  public void aceptarSolicitud(Long id) {
    Solicitud solicitud = this.getSolicitud(id);
    solicitud.aceptar();
    solicitudesEliminacionRepo.save(solicitud);
  }

  public Boolean hechoEliminado(Hecho hecho) {
    List <Solicitud> solicitudes = solicitudesEliminacionRepo.findAll();
    return solicitudes.stream().anyMatch(solicitud -> solicitud.getHecho().getId() == hecho.getId());
  }

  public Solicitud getSolicitud(Long solicitudId) {
    return solicitudesEliminacionRepo.findById(solicitudId).orElseThrow(() -> new EntityNotFoundException("Solicitud con id " + solicitudId + " no encontrado"));
  }

  public List<SolicitudDTOOutput> getSolicitudes() {
    return solicitudesEliminacionRepo.findAll().stream().map(solicitud -> solicitudConverter.fromEntity(solicitud)).toList();
  }

  public Integer cantidadSolicitudesSpam(Long idHecho) {
    AtomicReference<Integer> cantSolicitudesSpam = new AtomicReference<>(0);
    List<Solicitud> solicitudes = solicitudesEliminacionRepo.findAll().stream().filter(s -> s.esSpam()).toList();
    solicitudes.forEach(s -> {
      if (s.getHecho().getId() == idHecho) {
        cantSolicitudesSpam.getAndSet(cantSolicitudesSpam.get() + 1);
      }
    });
    return cantSolicitudesSpam.get();
  }

  public SolicitudDTOOutput getSolicitudDto(Long id) {
    Solicitud solicitud = getSolicitud(id);
    return solicitudConverter.fromEntity(solicitud);
  }
}