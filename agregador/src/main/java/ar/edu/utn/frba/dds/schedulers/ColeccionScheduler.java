package ar.edu.utn.frba.dds.schedulers;

import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.FuenteService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionScheduler {
  private ColeccionService coleccionesService;
  private FuenteService fuenteService;
  public ColeccionScheduler(ColeccionService coleccionesService, FuenteService fuenteService) {
    this.coleccionesService = coleccionesService;
    this.fuenteService = fuenteService;
  }

  //lo modifico ya que los hechos pertenecen a fuentes
  @Scheduled(fixedRate = 3600000) // 1 hora
  public void refrescarColecciones() {
    fuenteService.actualizarFuentes();
  }

  @Scheduled(fixedRate = 86400000) // 24 horas
  public void refrescarHechosCurados() {
    coleccionesService.refrescarHechosCurados();
  }
}
