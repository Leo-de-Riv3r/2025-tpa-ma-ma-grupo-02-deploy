package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
@Entity
@DiscriminatorValue("fecha_acontecimiento")
public class FiltroFechaAcontecimiento extends FiltroFecha {
  public FiltroFechaAcontecimiento(LocalDateTime inicio, LocalDateTime fin) {
    super(inicio, fin, Hecho::getFechaAcontecimiento, TipoFiltro.FILTRO_FECHA_ACONTECIMIENTO);
  }
}