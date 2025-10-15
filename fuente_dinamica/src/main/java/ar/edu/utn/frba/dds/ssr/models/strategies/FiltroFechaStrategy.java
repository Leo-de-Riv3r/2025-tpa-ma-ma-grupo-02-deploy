package ar.edu.utn.frba.dds.ssr.models.strategies;

import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class FiltroFechaStrategy implements IFiltroStrategy {
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaFinal;

  public FiltroFechaStrategy(LocalDateTime fechaInicio, LocalDateTime fechaFinal) {
    if (fechaInicio.isAfter(fechaFinal)) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
    this.fechaInicio = fechaInicio;
    this.fechaFinal = fechaFinal;
  }

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    return hecho.getFechaAcontecimiento().isAfter(fechaInicio) && hecho.getFechaAcontecimiento().isBefore(fechaFinal);
  }

  public void setFechaInicio(LocalDateTime fechaInicio) {
    if (fechaInicio.isAfter(this.fechaFinal)) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
    this.fechaInicio = fechaInicio;
  }

  public void setFechaFinal(LocalDateTime fechaFinal) {
    if (fechaFinal.isBefore(this.fechaInicio)) {
      throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
    }
    this.fechaFinal = fechaFinal;
  }
}
