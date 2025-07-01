package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Estado {
  private final String supervisor;
  private final TipoEstado estado;
  LocalDateTime fechaActualizacion;

  public Estado(String supervisor, TipoEstado estado) {
    this.fechaActualizacion = LocalDateTime.now();
    this.supervisor = supervisor;
    this.estado = estado;
  }
}
