package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.function.Function;

@Getter
public abstract class FiltroFecha implements IFiltroStrategy {

  private LocalDateTime fechaInicio;
  private LocalDateTime fechaFinal;
  private final Function<Hecho, LocalDateTime> extractorFecha;

  public FiltroFecha(LocalDateTime fechaInicio, LocalDateTime fechaFinal, Function<Hecho, LocalDateTime> extractorFecha) {
    if (fechaInicio != null && fechaFinal != null && fechaInicio.isAfter(fechaFinal)) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
    this.fechaInicio = fechaInicio;
    this.fechaFinal = fechaFinal;
    this.extractorFecha = extractorFecha;
  }

  @Override
  public boolean cumpleFiltro(Hecho hecho) {
    LocalDateTime fecha = extractorFecha.apply(hecho);

    if (fechaInicio != null && fechaFinal != null) {
      return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFinal);
    } else if (fechaInicio != null) {
      return !fecha.isBefore(fechaInicio);
    } else if (fechaFinal != null) {
      return !fecha.isAfter(fechaFinal);
    } else {
      return true;
    }
  }

  public void setFechaInicio(LocalDateTime fechaInicio) {
    if (fechaFinal != null && fechaInicio != null && fechaInicio.isAfter(fechaFinal)) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }
    this.fechaInicio = fechaInicio;
  }

  public void setFechaFinal(LocalDateTime fechaFinal) {
    if (fechaInicio != null && fechaFinal != null && fechaFinal.isBefore(fechaInicio)) {
      throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
    }
    this.fechaFinal = fechaFinal;
  }
}
