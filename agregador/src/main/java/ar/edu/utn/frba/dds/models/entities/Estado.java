package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import java.time.LocalDateTime;

public class Estado {
  private TipoEstado estado;
  LocalDateTime fechaActualizacion;
  private String supervisor;

  public Estado(String supervisor, TipoEstado estado) {
    this.fechaActualizacion = LocalDateTime.now();
    this.supervisor = supervisor;
    this.estado = estado;
  }
}
