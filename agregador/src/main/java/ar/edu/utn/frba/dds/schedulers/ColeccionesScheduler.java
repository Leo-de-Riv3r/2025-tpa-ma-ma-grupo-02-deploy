package ar.edu.utn.frba.dds.schedulers;

import ar.edu.utn.frba.dds.services.IColeccionesService;
import ar.edu.utn.frba.dds.services.ISolicitudesService;
import ar.edu.utn.frba.dds.services.impl.ColeccionesService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionesScheduler {
  private IColeccionesService coleccionesService;

  public ColeccionesScheduler(IColeccionesService coleccionesService) {
    this.coleccionesService = coleccionesService;
  }

  @Scheduled(fixedRate = 3600000) // 1 hora
  public void refrescarColecciones() {
    coleccionesService.refrescoColecciones();
  }
}
