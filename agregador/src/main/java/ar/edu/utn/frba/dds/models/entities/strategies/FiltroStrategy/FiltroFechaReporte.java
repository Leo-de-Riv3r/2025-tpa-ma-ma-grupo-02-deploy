package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.time.LocalDateTime;

public class FiltroFechaReporte extends FiltroFecha {
  public FiltroFechaReporte(LocalDateTime inicio, LocalDateTime fin) {
    super(inicio, fin, Hecho::getFechaCarga);
  }
}