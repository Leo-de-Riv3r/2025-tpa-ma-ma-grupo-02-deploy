package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.IpStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "blocked-ip")
public class BlockedIp {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String ip;
  @Column
  private LocalDateTime fechaModificacion = LocalDateTime.now();
  @Column
  private String motivo;
  @Enumerated
  @Column
  private IpStatus estado = IpStatus.BLOCKED;
}
