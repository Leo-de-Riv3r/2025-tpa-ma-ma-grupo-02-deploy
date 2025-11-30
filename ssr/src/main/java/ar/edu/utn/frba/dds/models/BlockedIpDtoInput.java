package ar.edu.utn.frba.dds.models;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BlockedIpDtoInput {
  private String ip;
  private String estado;
  private String motivo;
  private LocalDateTime fechaModificacion;
}
