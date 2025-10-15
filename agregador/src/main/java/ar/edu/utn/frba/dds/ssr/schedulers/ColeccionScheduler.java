package ar.edu.utn.frba.dds.ssr.schedulers;

import ar.edu.utn.frba.dds.ssr.services.ColeccionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionScheduler {
  private ColeccionService coleccionesService;
  public ColeccionScheduler(ColeccionService coleccionesService) {
    this.coleccionesService = coleccionesService;
  }

  //refresco fuentes directamente porque pueden repetirse entre colecciones
  @Scheduled(fixedDelay = 3600000, initialDelay = 3600000)
  public void refrescarColecciones() {
    coleccionesService.refrescoFuentes();
    coleccionesService.refrescarHechosFiltrados();
  }

  @Scheduled(fixedRate = 86400000) // 24 horas
  public void refrescarHechosCurados() {
    coleccionesService.refrescarHechosCurados();
  }

  //agregar cronjob para actualizar hechos filtrados
}
