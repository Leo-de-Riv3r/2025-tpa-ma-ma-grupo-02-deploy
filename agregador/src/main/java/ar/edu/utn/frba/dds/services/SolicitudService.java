package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.externalApi.SpamApi;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDto;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudResumenDtoOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import ar.edu.utn.frba.dds.models.entities.utils.SolicitudConverter;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    Solicitud solicitud = this.getSolicitudById(id);
    solicitud.marcarSpam();
    solicitudesEliminacionRepo.save(solicitud);
  }

  public void rechazarSolicitud(Long id) {
    Solicitud solicitud = this.getSolicitudById(id);
    solicitud.rechazar();
    solicitudesEliminacionRepo.save(solicitud);
  }

  public void aceptarSolicitud(Long id) {
    Solicitud solicitud = this.getSolicitudById(id);
    solicitud.aceptar();
    solicitudesEliminacionRepo.save(solicitud);
  }

  public Boolean hechoEliminado(Hecho hecho) {
    List <Solicitud> solicitudes = solicitudesEliminacionRepo.findAll();
    return solicitudes.stream().anyMatch(solicitud -> solicitud.getHecho().getId() == hecho.getId());
  }

    public PaginacionDto<SolicitudResumenDtoOutput> getSolicitudes(Integer page, Boolean pendientes, Boolean filterByCreator) {
    if (filterByCreator == null) filterByCreator = false;
    if (pendientes == null) pendientes = true;
      int pageNumber = (page == null || page < 1) ? 0 : page - 1;
      int pageSize = 20;
      Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("fecha").descending());

      Page<Solicitud> pageResult;

      // Si se debe filtrar por creador
      if (filterByCreator) {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
          username = userDetails.getUsername();
        } else {
          username = authentication.getPrincipal().toString();
        }

        // Filtrado según creador y estado
        if (pendientes) {
          pageResult = solicitudesEliminacionRepo.findByCreadorAndEstadoActual(username, TipoEstado.PENDIENTE, pageable);
        } else {
          pageResult = solicitudesEliminacionRepo.findByCreador(username, pageable);
        }

      } else {
        // Sin filtro por creador
        if (pendientes) {
          pageResult = solicitudesEliminacionRepo.findByEstadoActual(TipoEstado.PENDIENTE, pageable);
        } else {
          pageResult = solicitudesEliminacionRepo.findAll(pageable);
        }
      }

      List<SolicitudResumenDtoOutput> solicitudesDto = pageResult.getContent()
          .stream()
          .map(solicitudConverter::fromEntity)
          .toList();

      return new PaginacionDto<>(
          solicitudesDto,
          pageable.getPageNumber() + 1,
          pageResult.getTotalPages()
      );
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

  public Solicitud getSolicitudById(Long id) {
    return solicitudesEliminacionRepo.findById(id).orElseThrow( ()-> new EntityNotFoundException("Solicitud no encontrada"));
  }
  public SolicitudDTOOutput getSolicitudDto(Long id) {
    Solicitud solicitud = getSolicitudById(id);
    return solicitudConverter.fromEntityDetails(solicitud);
  }
}