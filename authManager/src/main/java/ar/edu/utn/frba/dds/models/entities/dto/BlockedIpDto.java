package ar.edu.utn.frba.dds.models.entities.dto;

import ar.edu.utn.frba.dds.models.IpStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class BlockedIpDto {
  private String ip;
  private IpStatus estado;
  private String motivo;
  private LocalDateTime fechaModificacion;
}
