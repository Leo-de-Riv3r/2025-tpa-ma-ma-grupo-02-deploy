package ar.edu.utn.frba.dds.schedulers;

import ar.edu.utn.frba.dds.services.ColeccionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class ColeccionScheduler {
  private final ColeccionService coleccionService;

  public ColeccionScheduler(ColeccionService coleccionService) {
    this.coleccionService = coleccionService;
  }

  @Scheduled(fixedDelay = 3600000, initialDelay = 3600000)
  public void refrescarColecciones() {
    coleccionService.refrescoFuentes();
  }

  @Scheduled(cron = "${scheduler.cron.curaduria}", zone = "America/Argentina/Buenos_Aires")
  public void refrescarHechosCurados() {
    log.info("SCHEDULER: Iniciando ciclo de curaduría de hechos...");
    List<String> ids = coleccionService.obtenerTodosLosIdsColecciones();
    for (String id : ids) {
      try {
        coleccionService.refrescarHechosCurados(id);
      } catch (Exception e) {
        log.error("Error al refrescar colección ID {}: {}", id, e.getMessage());
      }
    }
    log.info("SCHEDULER: Ciclo de curaduría finalizado.");
  }

  // @Scheduled(fixedRate = 300000)
  // public void tareaDeRescateColecciones() {
  //   coleccionService.procesarColeccionesPendientes();
  // }
}