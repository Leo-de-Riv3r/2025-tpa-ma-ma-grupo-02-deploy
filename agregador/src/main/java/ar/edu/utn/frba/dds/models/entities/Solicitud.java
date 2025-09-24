package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solicitud")
public class Solicitud {
  @Id
  private Long id;
  @Column
  @Getter
  @Setter
  private String titulo;
  @Column
  @Getter
  @Setter
  private String texto;
  //aca podria tener hecho y hacer un many to one
  @Column
  @Getter
  @Setter
  private String tituloHecho;

  @Column
  @Setter
  @Getter
  private LocalDateTime fecha;
  @Column
  @Getter
  @Setter
  private String responsable;
  @Column
  @Getter
  @Setter
  private String supervisor;
  //one to many
  @Getter
  @Setter
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "estado_id", referencedColumnName = "id")
  private Estado estadoActual;
  @Getter
  @OneToMany(mappedBy = "solicitud")
  private List<Estado> historial = new ArrayList<>();
  @Column
  private Integer spam;

  @ManyToOne
  @JoinColumn(name="hecho_id", referencedColumnName = "id")
  private Hecho hecho;

  public Solicitud() {
    Estado estado = new Estado();
    estado.setSolicitud(this);
    this.estadoActual = estado;
    this.supervisor = "";
    this.responsable = "";
    this.fecha = LocalDateTime.now();
    this.spam = 0;
  }

  public Boolean estaFundado() {
    return texto.length() >= 500;
  }

  public void rechazar(String supervisor) {
    Estado nuevoEstado = new Estado();
    nuevoEstado.setSupervisor(supervisor);
    nuevoEstado.setEstado(TipoEstado.RECHAZADA);
    this.cambiarEstado(nuevoEstado);
  }

  public void aceptar(String supervisor) {
    Estado nuevoEstado = new Estado();
    nuevoEstado.setSupervisor(supervisor);
    nuevoEstado.setEstado(TipoEstado.ACEPTADA);
    this.cambiarEstado(nuevoEstado);
  }

  private void cambiarEstado(Estado estado) {
    this.historial.add(this.estadoActual);
    estado.setSolicitud(this);
    this.estadoActual = estado;
  }

  public boolean estaAceptada() {
    return this.estadoActual.getEstado() == TipoEstado.ACEPTADA;
  }

  public void marcarSpam() {
    this.spam = 1;
  }
  public boolean esSpam() {
    return this.spam == 1;
  }
}