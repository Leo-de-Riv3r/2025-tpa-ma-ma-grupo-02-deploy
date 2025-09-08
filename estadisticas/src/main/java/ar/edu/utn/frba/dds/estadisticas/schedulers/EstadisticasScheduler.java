package ar.edu.utn.frba.dds.estadisticas.schedulers;

import ar.edu.utn.frba.dds.estadisticas.services.IEstadisticasService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EstadisticasScheduler {
  private IEstadisticasService estadisticasService;
  public EstadisticasScheduler(IEstadisticasService estadisticasService) {
    this.estadisticasService = estadisticasService;
  }

  @Scheduled(fixedRate = 3600000) // 1 hora
  public void refrescarColecciones() {
    estadisticasService.actualizarEstadisticas();
  }
}
