package ar.edu.utn.frba.dds.entities;

import ar.edu.utn.frba.dds.enums.Estado;
import java.time.LocalDate;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Solicitud {
  private String titulo;
  private String texto;
  private Hecho hecho;
  private Estado estado;
  private LocalDateTime fecha;
  private String responsable;
  private String supervisor;

  public Solicitud(String titulo, String texto, Hecho hecho, String responsable) {
    this.titulo = titulo;
    this.texto = texto;
    this.hecho = hecho;
    this.estado = Estado.PENDIENTE;
    this.fecha = LocalDateTime.now();
    this.responsable = responsable;
  }

  public Boolean estaFundado() {
    return texto.length() >= 500;
  }

  public void rechazar() {
    // TODO: Implementar
  }

  public void aceptar() {
    // TODO: Implementar
  }
}