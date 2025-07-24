package ar.edu.utn.frba.dds.servicies.impl;

import ar.edu.utn.frba.dds.mappers.HechoMapper;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.MultimediaInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudModificacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Categoria;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.enums.Formato;
import ar.edu.utn.frba.dds.models.enums.Motivo;
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
  private IHechosRepository hechosRepository;
  private IContribuyenteRepository contribuyenteRepository;
  private ISolicitudesRepository solicitudesRepository;

  public HechosService(
          IHechosRepository hechosRepository,
          IContribuyenteRepository contribuyenteRepository,
          ISolicitudesRepository solicitudesRepository) {
    this.hechosRepository = hechosRepository;
    this.contribuyenteRepository = contribuyenteRepository;
    this.solicitudesRepository = solicitudesRepository;
  }

  @Override
  public void modificarHecho(SolicitudModificacionInputDTO solicitudModificacion) {
    var hecho = hechosRepository.findById(solicitudModificacion.getHecho().getId());
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

  private void agregarNuevaSolicitud(SolicitudModificacionInputDTO _solicitud, Hecho hecho) {
    var solicitud = new Solicitud(_solicitud.getTitulo(), _solicitud.getTexto(), hecho, _solicitud.getContribuyente().getNombre());
    solicitud.setMotivo(Motivo.MODIFICACION);
    solicitudesRepository.save(solicitud);
  }

  private boolean sePuedeEditarHecho(Hecho hecho) {
    var fechaLimite = hecho.getFechaCarga().plusWeeks(1);
    return hecho.getFechaCarga().isBefore(fechaLimite);
  }

  @Override
  public List<HechoOutputDTO> getHechos() {
    List<Hecho> hechos = hechosRepository.findAll();

    return hechos.stream()
        .map(HechoMapper::toHechoOutputDTO)
        .collect(Collectors.toList());
  }

  @Override
  public HechoOutputDTO getHechoById(Long id) {
    Hecho hecho = hechosRepository.findById(id);
    if (hecho == null) {
      throw new RuntimeException("No existe hecho con ese id");
    }
    return HechoMapper.toHechoOutputDTO(hecho);
  }

  @Override
  public HechoOutputDTO crearHecho(HechoInputDTO hechoDto) {
    Hecho hecho = Hecho.builder()
        .titulo(hechoDto.getTitulo())
        .descripcion(hechoDto.getDescripcion())
        .categoria(new Categoria(hechoDto.getCategoria()))
        .ubicacion(new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud()))
        .fechaAcontecimiento(LocalDateTime.parse(hechoDto.getFechaAcontecimiento(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        .build();

      hechoDto.getMultimedia().forEach(multimediaInputDTO -> {
            Multimedia multimediaDto = new Multimedia(multimediaInputDTO.getNombre(), multimediaInputDTO.getRuta(), Formato.fromString(multimediaInputDTO.getFormato()));
            hecho.addMultimedia(multimediaDto);
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
}

