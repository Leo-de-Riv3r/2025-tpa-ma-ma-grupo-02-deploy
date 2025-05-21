package ar.edu.utn.frba.dds.schedulers;

import ar.edu.utn.frba.dds.services.IAgregadorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionesScheduler {
  private final IAgregadorService agregadorService;

  public ColeccionesScheduler(IAgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }

  @Scheduled(fixedRate = 3600000) // 1 hora
  public void refrescarColecciones() {
    agregadorService.refrescoColecciones();
  }
}
