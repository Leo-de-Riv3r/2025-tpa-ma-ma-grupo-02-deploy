package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import java.time.LocalDateTime;

public class Estado {
  private TipoEstado estado;
  LocalDateTime fechaActualizacion;
  private String supervisor;
}
