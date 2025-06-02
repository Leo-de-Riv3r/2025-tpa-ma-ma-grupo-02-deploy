package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.IFuenteAdapter;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.repositories.SolicitudesRepository;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudesRepository;
import ar.edu.utn.frba.dds.services.ISolicitudesService;
import ar.edu.utn.frba.dds.services.IDetectorSpam;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
      this.rechazarSolicitud(solicitud, "automatico");
    }
  }

  @Override
  public void rechazarSolicitud(Solicitud solicitud, String supervisor)  {
    hechosSolicitudesRepository.rechazarSolicitud(solicitud, supervisor);
  }

  @Override
  public void aceptarSolicitud(Solicitud solicitud, String supervisor) {
    hechosSolicitudesRepository.aceptarSolicitud(solicitud, supervisor);
  }

//
}