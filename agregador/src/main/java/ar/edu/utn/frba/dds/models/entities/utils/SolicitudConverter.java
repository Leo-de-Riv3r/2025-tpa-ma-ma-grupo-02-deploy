package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.input.SolicitudDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudResumenDtoOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SolicitudConverter {
  private final IHechoRepository hechoRepository;
  public SolicitudConverter(IHechoRepository hechoRepository) {
    this.hechoRepository = hechoRepository;
  }

  public Solicitud fromDto(SolicitudDTOEntrada dtoSolicitud) {
    if (dtoSolicitud.getTitulo() == null || dtoSolicitud.getTitulo().isBlank() ||
    dtoSolicitud.getTexto() == null || dtoSolicitud.getIdHecho() == null) {
      throw new IllegalArgumentException("Falta llenar campos obligatorios");
    }
    Solicitud solicitud = new Solicitud();
    solicitud.setTitulo(dtoSolicitud.getTitulo());
    solicitud.setTexto(dtoSolicitud.getTexto());
    //buscar id de hecho
    Optional<Hecho> hecho = hechoRepository.findById(dtoSolicitud.getIdHecho());
    if (hecho.isPresent()) {
      solicitud.setHecho(hecho.get());
    } else {
      throw new EntityNotFoundException("Hecho con id " + dtoSolicitud.getIdHecho());
    }
    if(dtoSolicitud.getCreador() != null) solicitud.setCreador(dtoSolicitud.getCreador());

    return solicitud;
  }

  public SolicitudResumenDtoOutput fromEntity(Solicitud solicitud) {
    SolicitudResumenDtoOutput dto = new SolicitudResumenDtoOutput();
    dto.setId(solicitud.getId());
    dto.setTitulo(solicitud.getTitulo());
    dto.setFecha(solicitud.getFecha());
    dto.setEstadoActual(solicitud.getEstadoActual().getEstado().toString());
    dto.setEsSpam(solicitud.getSpam());

    return dto;
  }

  public SolicitudDTOOutput fromEntityDetails(Solicitud solicitud) {
    SolicitudDTOOutput dto = new SolicitudDTOOutput();
    dto.setId(solicitud.getId());
    dto.setIdHecho(solicitud.getHecho().getId());
    dto.setTitulo(solicitud.getTitulo());
    dto.setMotivo(solicitud.getTexto());
    dto.setFecha(solicitud.getFecha());
    dto.setEstadoActual(solicitud.getEstadoActual().getEstado().toString());
    dto.setEsSpam(solicitud.getSpam());
    dto.setCreador(solicitud.getCreador());

    return dto;
  }
}