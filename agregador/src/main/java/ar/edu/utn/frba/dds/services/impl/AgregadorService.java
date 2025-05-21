package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.HechosSolicitudesRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosSolicitudesRepository;
import ar.edu.utn.frba.dds.services.IAgregadorService;
import ar.edu.utn.frba.dds.services.IDetectorSpam;
import org.springframework.stereotype.Service;

@Service
public class AgregadorService implements IAgregadorService {
  private IHechosSolicitudesRepository hechosSolicitudesRepository = new HechosSolicitudesRepository();
  private IDetectorSpam detectorSpam;

  @Override
  public void actualizarColecciones() {
  }

  @Override
  public void createSolicitud(Solicitud solicitud) throws Exception {
    hechosSolicitudesRepository.createSolicitud(solicitud);
    if(!detectorSpam.esSpam(solicitud.getTexto())) {
      this.rechazarSolicitud(solicitud, "automatico");
    }
  }

  public void rechazarSolicitud(Solicitud solicitud, String supervisor)  {
    hechosSolicitudesRepository.rechazarSolicitud(solicitud, supervisor);
  }
}
