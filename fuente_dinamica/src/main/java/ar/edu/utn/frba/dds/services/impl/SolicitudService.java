package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.dtos.input.RespuestaSolicitudInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudEliminacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudEliminacionOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.enums.EstadoSolicitud;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudesRepository;
import ar.edu.utn.frba.dds.services.ISolicitudesService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SolicitudService implements ISolicitudesService {
    private final ISolicitudesRepository solicitudesRepository;
    private final IHechosRepository hechosRepository;

    public SolicitudService(ISolicitudesRepository solicitudesRepository, IHechosRepository hechosRepository) {
        this.solicitudesRepository = solicitudesRepository;
        this.hechosRepository = hechosRepository;
    }

    @Override
    public List<SolicitudEliminacionOutputDTO> getSolicitudesPendientes() {
        List<Solicitud> solicitudesPendientes = solicitudesRepository.findSolicitudesPendientes();

        return solicitudesPendientes.stream()
                .map(this::toSolicitudEliminacionOutputDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudEliminacionOutputDTO crearSolicitudEliminacion(SolicitudEliminacionInputDTO solicitudDto) {
        Hecho hecho = hechosRepository.findById(solicitudDto.getHechoId())
                .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + solicitudDto.getHechoId()));

        if (solicitudDto.getTexto() == null || solicitudDto.getTexto().length() < 500) {
            throw new RuntimeException("La solicitud debe estar fundada con al menos 500 caracteres");
        }

        if (hecho.getEliminado()) {
            throw new RuntimeException("Este hecho ya ha sido eliminado");
        }

        Solicitud solicitud = Solicitud.builder()
                .titulo(solicitudDto.getTitulo())
                .texto(solicitudDto.getTexto())
                .hecho(hecho)
                .estadoSolicitud(EstadoSolicitud.PENDIENTE)
                .responsable(solicitudDto.getResponsable())
                .build();

        Solicitud solicitudGuardada = solicitudesRepository.save(solicitud);

        return toSolicitudEliminacionOutputDTO(solicitudGuardada);
    }

    @Override
    public SolicitudEliminacionOutputDTO aceptarSolicitud(Long solicitudId, RespuestaSolicitudInputDTO respuestaDto) {
        Solicitud solicitud = solicitudesRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("La solicitud ya fue procesada anteriormente.");
        }
        // Actualizamos solicitud
        solicitud.aceptar();
        solicitud.setSupervisor(respuestaDto.getSupervisor());

        Hecho hecho = solicitud.getHecho();
        hecho.setEliminado(true);

        // Guardamos la solicitud con los nuevos datos. (Se deberían guardar de nuevo?)
        solicitudesRepository.save(solicitud);
        hechosRepository.save(hecho);

        return toSolicitudEliminacionOutputDTO(solicitud);
    }

    @Override
    public SolicitudEliminacionOutputDTO rechazarSolicitud(Long solicitudId, RespuestaSolicitudInputDTO respuestaDto) {
        Solicitud solicitud = solicitudesRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("La solicitud ya fue procesada anteiormente.");
        }

        solicitud.rechazar();
        solicitud.setSupervisor(respuestaDto.getSupervisor());

        if (respuestaDto.getComentario() != null) {
            solicitud.setTexto(solicitud.getTexto() + "\n Comentario del admin: \n" + respuestaDto.getComentario());
        }
        solicitudesRepository.save(solicitud);

        return toSolicitudEliminacionOutputDTO(solicitud);
    }

    private SolicitudEliminacionOutputDTO toSolicitudEliminacionOutputDTO(Solicitud solicitud) {
        return SolicitudEliminacionOutputDTO.builder()
                .id(solicitud.getId())
                .titulo(solicitud.getTitulo())
                .texto(solicitud.getTexto())
                .estado(solicitud.getEstadoSolicitud())
                .fecha(solicitud.getFecha())
                .responsable(solicitud.getResponsable())
                .supervisor(solicitud.getSupervisor())
                .hechoId(solicitud.getHecho().getId())
                .build();
    }


}
