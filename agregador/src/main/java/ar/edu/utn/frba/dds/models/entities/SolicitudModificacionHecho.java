package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.EstadoSolicitudModificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solicitud_modificacion_hecho")
public class SolicitudModificacionHecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name="hecho_id", referencedColumnName = "id")
  private Hecho hecho;
  @Column
  private String titulo;
  @Column
  private String descripcion;
  @Column
  private String categoria;
  @Column
  private Double latitud;
  @Column
  private Double longitud;
  @Column
  private LocalDateTime fechaAcontecimiento;
  @Column
  private String creador;
  @Column
  private LocalDateTime fecha = LocalDateTime.now();
  @Enumerated(EnumType.STRING)
  @Column
  private EstadoSolicitudModificacion estadoSolicitudModificacion = EstadoSolicitudModificacion.PENDIENTE;

  public void aceptar() {
    this.estadoSolicitudModificacion = EstadoSolicitudModificacion.ACEPTADA;
  }

  public void rechazar() {
    this.estadoSolicitudModificacion = EstadoSolicitudModificacion.RECHAZADA;
  }
}
