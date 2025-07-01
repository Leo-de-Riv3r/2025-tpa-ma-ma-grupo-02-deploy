package ar.edu.utn.frba.dds.schedulers;

import ar.edu.utn.frba.dds.services.ColeccionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionScheduler {
  private ColeccionService coleccionesService;

  public ColeccionScheduler(ColeccionService coleccionesService) {
    this.coleccionesService = coleccionesService;
  }

  @Scheduled(fixedRate = 3600000) // 1 hora
  public void refrescarColecciones() {
    coleccionesService.refrescoColecciones();
  }

  @Scheduled(fixedRate = 86400000) // 24 horas
  public void refrescarHechosCurados() {
    coleccionesService.refrescarHechosCurados();
  }
}
