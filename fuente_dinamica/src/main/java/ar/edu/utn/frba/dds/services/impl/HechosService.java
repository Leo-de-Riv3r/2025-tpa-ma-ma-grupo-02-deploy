package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.mappers.HechoMapper;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoUpdateDTO;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoRevisionOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.MultimediaOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.services.IHechosService;

import ar.edu.utn.frba.dds.services.S3Service;
import java.time.LocalDateTime;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.utn.frba.dds.services.IMultimediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
public class HechosService implements IHechosService {
  private final IHechosRepository hechosRepository;
  private final IMultimediaService multimediaService;
  private final S3Service s3Service;
  @Value("${modification.allowance-days}")
  private long DIAS_EDICION_PERMITIDOS;

  public HechosService(
      IHechosRepository hechosRepository,
      IMultimediaService multimediaService, S3Service s3Service) {
    this.hechosRepository = hechosRepository;
    this.multimediaService = multimediaService;
    this.s3Service = s3Service;
  }

  private boolean sePuedeEditarHecho(Hecho hecho) {
    var fechaLimite = hecho.getFechaCarga().plusDays(DIAS_EDICION_PERMITIDOS);
    return hecho.getFechaCarga().isBefore(fechaLimite);
  }

  @Override
  public List<HechoOutputDTO> getHechos() {
    List <Hecho> hechos = hechosRepository.findHechosAceptados();

    return hechos.stream()
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
    public HechoOutputDTO crearHecho(HechoInputDTO hechoDto, List<MultipartFile> multimedia) {
        Hecho hecho = Hecho.builder()
                .titulo(hechoDto.getTitulo())
                .descripcion(hechoDto.getDescripcion())
                .categoria(hechoDto.getCategoria())
                .ubicacion(new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud()))
                .fechaAcontecimiento(hechoDto.getFechaAcontecimiento())
                .nombreAutor(hechoDto.getAutor())
                .build();

        if (multimedia != null && !multimedia.isEmpty()) {
            List<MultipartFile> validFiles = multimedia.stream()
                    .filter(f -> f != null && !f.isEmpty())
                    .toList();

            if (!validFiles.isEmpty()) {
                validFiles.forEach(file -> {
                    try {
                        Multimedia multimediaEntity = multimediaService.guardarArchivo(file);
                        hecho.addMultimedia(multimediaEntity);
                    } catch (IOException | IllegalArgumentException e) {
                        throw new RuntimeException("Error al procesar archivo multimedia: " + e.getMessage(), e);
                    }
                });
            }
        }
        Hecho hechoGuardado = hechosRepository.save(hecho);

        List<MultimediaOutputDTO> multimediaDto = hechoGuardado.getMultimedia().stream().map(m -> MultimediaOutputDTO.builder()
                        .nombre(m.getNombre())
                        .ruta(m.getRuta())
                        .formato(m.getFormato().name().toLowerCase())
                        .build()).toList();
        log.info("Nuevo hecho guardado: Titulo: {}, Categoria: {}", hechoGuardado.getTitulo(), hechoGuardado.getCategoria());
        return HechoOutputDTO.builder()
                .titulo(hechoGuardado.getTitulo())
                .descripcion(hechoGuardado.getDescripcion())
                .categoria(hechoGuardado.getCategoria())
                .latitud(hechoGuardado.getUbicacion().getLatitud())
                .longitud(hechoGuardado.getUbicacion().getLongitud())
                .fechaHecho(hechoGuardado.getFechaAcontecimiento())
                .createdAt(hechoGuardado.getFechaCarga())
                .updatedAt(hechoGuardado.getFechaUltimaModificacion())
                .multimedia(multimediaDto)
                .autor(hechoGuardado.getNombreAutor())
                .build();
    }

  @Override
  public HechoOutputDTO actualizarHecho(Long id, HechoUpdateDTO hechoDto, List<MultipartFile> multimedia, String username) {

    Hecho hecho = hechosRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hecho no encontrado"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMINISTRADOR"));

    System.out.println("Autor: " + hecho.getNombreAutor());
    System.out.println("Es admin:" + isAdmin);
    if (!isAdmin && !username.equals(hecho.getNombreAutor())) {
      throw new IllegalStateException("No tienes permiso para editar este hecho");
    }

    if (!sePuedeEditarHecho(hecho)) {
      throw new IllegalStateException(String.format("El período de edición ha expirado. Solo puedes editar los hechos dentro de los %d dias posteriores a su edición", DIAS_EDICION_PERMITIDOS));
    }

    if (hechoDto.getTitulo() != null) {
      hecho.setTitulo(hechoDto.getTitulo());
    }
    if (hechoDto.getDescripcion() != null) {
      hecho.setDescripcion(hechoDto.getDescripcion());
    }
    if (hechoDto.getCategoria() != null) {
      hecho.setCategoria(hechoDto.getCategoria());
    }
    if (hechoDto.getLatitud() != null) {
      hecho.getUbicacion().setLatitud(hechoDto.getLatitud());
    }
    if (hechoDto.getLongitud() != null) {
      hecho.getUbicacion().setLongitud(hechoDto.getLongitud());
    }
    hecho.setFechaUltimaModificacion(LocalDateTime.now());

    hecho.getMultimedia().clear();

    if (multimedia != null && !multimedia.isEmpty()) {
        List<MultipartFile> validFiles = multimedia.stream()
                .filter(f -> f != null && !f.isEmpty())
                .toList();

        if (!validFiles.isEmpty()) {
            validFiles.forEach(file -> {
                try {
                    Multimedia multimediaEntity = multimediaService.guardarArchivo(file);
                    hecho.addMultimedia(multimediaEntity);
                } catch (IOException | IllegalArgumentException e) {
                    throw new RuntimeException("Error al procesar archivo multimedia: " + e.getMessage(), e);
                }
            });
        }
    }

    Hecho hechoActualizado = hechosRepository.save(hecho);

    return HechoMapper.toHechoOutputDTO(hechoActualizado);
  }

  // Métodos para revisión de admins

  @Override
  public List<HechoRevisionOutputDTO> getHechosPendientes() {
    List<Hecho> hechosPendientes = hechosRepository.findHechosPendientes();

    return hechosPendientes.stream()
        .map(this::toHechoRevisionOutputDTO)
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
    log.info("Hecho aceptado: Titulo: {}, Categoria: {}", hechoActualizado.getTitulo(), hechoActualizado.getCategoria());
    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  @Override
  public HechoRevisionOutputDTO aceptarHechoConSugerencias(Long id, RevisionInputDTO revisionDto) {
    Hecho hecho = hechosRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + id));

    if (!hecho.estaPendiente()) {
      throw new RuntimeException("El hecho ya fue revisado anteriormente");
    }
    hecho.aceptarConSugerencias(revisionDto.getSupervisor(), revisionDto.getComentario());
    Hecho hechoActualizado = hechosRepository.save(hecho);
  log.info("Hecho aceptado: Titulo: {}, Categoria: {}", hechoActualizado.getTitulo(), hechoActualizado.getCategoria());
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
    log.info("Hecho rechazado: Titulo: {}, Categoria: {}", hechoActualizado.getTitulo(), hechoActualizado.getCategoria());
    return toHechoRevisionOutputDTO(hechoActualizado);
  }

  @Override
  public List<HechoRevisionOutputDTO> getHechosPendientesByCreador() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username;
    if (authentication.getPrincipal() instanceof UserDetails userDetails) {
      username = userDetails.getUsername();
    } else {
      username = authentication.getPrincipal().toString();
    }

    List<Hecho> hechosPendientes = hechosRepository.findHechosPendientesByCreator(username);

    return hechosPendientes.stream()
            .map(this::toHechoRevisionOutputDTO)
            .collect(Collectors.toList());
  }

  private HechoRevisionOutputDTO toHechoRevisionOutputDTO(Hecho hecho) {
    return HechoRevisionOutputDTO.builder()
            .id(hecho.getId())
            .titulo(hecho.getTitulo())
            .descripcion(hecho.getDescripcion())
            .categoria(hecho.getCategoria())
            .latitud(hecho.getUbicacion().getLatitud())
            .longitud((hecho.getUbicacion().getLongitud()))
            .fecha_acontecimiento(hecho.getFechaAcontecimiento())
            .fecha_carga(hecho.getFechaCarga())
            .estado_hecho(hecho.getEstadoHecho())
            .motivo_rechazo(hecho.getMotivoRechazo())
            .sugerencias(hecho.getSugerencias())
            .fecha_revision(LocalDateTime.now())
            .revisado_por(hecho.getRevisadoPor())
            .build();
  }
}

