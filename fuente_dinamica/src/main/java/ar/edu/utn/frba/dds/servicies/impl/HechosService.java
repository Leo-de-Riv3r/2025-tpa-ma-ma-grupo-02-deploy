package ar.edu.utn.frba.dds.servicies.impl;

import ar.edu.utn.frba.dds.mappers.HechoMapper;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.MultimediaInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudModificacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoRevisionOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Categoria;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.enums.Formato;
import ar.edu.utn.frba.dds.models.repositories.IContribuyenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudesRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import ar.edu.utn.frba.dds.utils.ContribucionUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class HechosService implements IHechosService {
  private final IHechosRepository hechosRepository;
  private final IContribuyenteRepository contribuyenteRepository;
  private final ISolicitudesRepository solicitudesRepository;

  public HechosService(
          IHechosRepository hechosRepository,
          IContribuyenteRepository contribuyenteRepository,
          ISolicitudesRepository solicitudesRepository) {
    this.hechosRepository = hechosRepository;
    this.contribuyenteRepository = contribuyenteRepository;
    this.solicitudesRepository = solicitudesRepository;
  }

  /*@Override
  public void modificarHecho(SolicitudModificacionInputDTO solicitudModificacion) {
    var hecho = hechosRepository
            .findById(solicitudModificacion.getHecho().getId())
            .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + solicitudModificacion.getHecho().getId()));

    var contribuyenteDto = solicitudModificacion.getContribuyente();

    if (!ContribucionUtils.tieneCredenciales(contribuyenteDto)) {
      hecho.actualizarDesde(solicitudModificacion.getHecho());
      agregarNuevaSolicitud(solicitudModificacion, hecho);
      return;
    }

    var contribuyente = contribuyenteRepository.findByEmail(contribuyenteDto.getEmail());

    if (sePuedeEditarHecho(hecho) && contribuyente.getPassword().equals(contribuyenteDto.getPassword())) {
      hecho.actualizarDesde(solicitudModificacion.getHecho());
      hechosRepository.save(hecho);
      return;
    }

    hecho.actualizarDesde(solicitudModificacion.getHecho());
    agregarNuevaSolicitud(solicitudModificacion, hecho);
  }

   */

  private void agregarNuevaSolicitud(SolicitudModificacionInputDTO solicitudDTO, Hecho hecho) {
      var solicitud = Solicitud.builder()
              .titulo(solicitudDTO.getTitulo())
              .texto(solicitudDTO.getTexto())
              .hecho(hecho)
              .responsable(solicitudDTO.getContribuyente().getNombre())
              .build();

      solicitudesRepository.save(solicitud);
  }

  private boolean sePuedeEditarHecho(Hecho hecho) {
    var fechaLimite = hecho.getFechaCarga().plusWeeks(1);
    return hecho.getFechaCarga().isBefore(fechaLimite);
  }

  @Override
  public List<HechoOutputDTO> getHechos() {
    List<Hecho> hechos = hechosRepository.findHechosAceptados();

    return hechos.stream()
            .filter(hecho -> !hecho.getEliminado())
            .map(HechoMapper::toHechoOutputDTO)
            .collect(Collectors.toList());
  }

  @Override
  public HechoOutputDTO getHechoById(Long id) {
    Hecho hecho = hechosRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    return HechoMapper.toHechoOutputDTO(hecho);
  }

  @Override
  public HechoOutputDTO crearHecho(HechoInputDTO hechoDto) {
    Hecho hecho = Hecho.builder()
        .titulo(hechoDto.getTitulo())
        .descripcion(hechoDto.getDescripcion())
        .categoria(Categoria.builder()
                .nombre(hechoDto.getCategoria())
                .build())
        .ubicacion(new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud()))
        .fechaAcontecimiento(LocalDateTime.parse(hechoDto.getFechaAcontecimiento(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        .build();
        // TODO: Armar repositorio para multimedia y persistirlo
      hechoDto.getMultimedia().forEach(multimediaInputDTO -> {
          var multimedia = Multimedia.builder()
                  .nombre(multimediaInputDTO.getNombre())
                  .ruta(multimediaInputDTO.getRuta())
                  .formato(Formato.fromString(multimediaInputDTO.getFormato()))
                  .build();
            hecho.addMultimedia(multimedia);
      }
      );
    Hecho hechoGuardado = hechosRepository.save(hecho);

    return HechoOutputDTO.builder()
        .titulo(hechoGuardado.getTitulo())
        .descripcion(hechoGuardado.getDescripcion())
        .categoria(hechoGuardado.getCategoria().getNombre())
        .latitud(hechoGuardado.getUbicacion().getLatitud())
        .longitud(hechoGuardado.getUbicacion().getLongitud())
        .fecha_hecho(hechoGuardado.getFechaAcontecimiento())
        .created_at(hechoGuardado.getFechaCarga())
        .build();
  }

  // Métodos para revisión de admins

  @Override
  public List<HechoRevisionOutputDTO> getHechosPendientes() {
    List<Hecho> hechosPendientes = hechosRepository.findHechosPendientes();

    return hechosPendientes.stream()
        .map((this::toHechoRevisionOutputDTO))
        .collect(Collectors.toList());
  }

  @Override
  public HechoRevisionOutputDTO aceptarHecho(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Hecho no encontrado con id: " + id));

    if (!hecho.estaPendiente()) {
      throw new RuntimeException("El hecho ya fue revisado anteriormente");
    }

    hecho.aceptar(revisionDto.getSupervisor());
    Hecho hechoActualizado = hechosRepository.save(hecho);

    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  @Override
  public HechoRevisionOutputDTO aceptarHechoConSugerencias(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    if (!hecho.estaPendiente()) {
      throw new RuntimeException("El hecho ya fue revisado anteriormente");
    }

    if (hecho.getEliminado()) {
      throw new RuntimeException("No se puede aceptar un hecho eliminado");
    }

    hecho.aceptarConSugerencias(revisionDto.getSupervisor(), revisionDto.getComentario());
    Hecho hechoActualizado = hechosRepository.save(hecho);

    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  @Override
  public HechoRevisionOutputDTO rechazarHecho(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    if (!hecho.estaPendiente()) {
      throw new RuntimeException("El hecho ya fue revisado anteriormente");
    }

    hecho.rechazar(revisionDto.getSupervisor(), revisionDto.getComentario());
    Hecho hechoActualizado = hechosRepository.save(hecho);

    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  private HechoRevisionOutputDTO toHechoRevisionOutputDTO(Hecho hecho) {
    return HechoRevisionOutputDTO.builder()
            .id(hecho.getId())
            .titulo(hecho.getTitulo())
            .descripcion(hecho.getDescripcion())
            .categoria(hecho.getCategoria().getNombre())
            .latitud(hecho.getUbicacion().getLatitud())
            .longitud((hecho.getUbicacion().getLongitud()))
            .fecha_acontecimiento(hecho.getFechaAcontecimiento())
            .fecha_carga(hecho.getFechaCarga())
            .estado_hecho(hecho.getEstadoHecho())
            .motivo_rechazo(hecho.getMotivoRechazo())
            .sugerencias(hecho.getSugerencias())
            .fecha_revision(null) //debería agregarlo a hecho?
            .revisado_por(hecho.getRevisadoPor())
            .build();
  }
}

