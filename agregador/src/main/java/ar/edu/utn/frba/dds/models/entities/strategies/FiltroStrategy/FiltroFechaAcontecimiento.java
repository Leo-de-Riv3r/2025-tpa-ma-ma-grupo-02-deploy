package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue(value = "FILTRO_FECHA_ACONTECIMIENTO")
@NoArgsConstructor
public class FiltroFechaAcontecimiento extends FiltroFecha {
  public FiltroFechaAcontecimiento(LocalDateTime inicio, LocalDateTime fin) {
    super(inicio, fin, Hecho::getFechaAcontecimiento, TipoFiltro.FILTRO_FECHA_ACONTECIMIENTO);
  }
  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    LocalDateTime fecha = hecho.getFechaAcontecimiento();

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
}