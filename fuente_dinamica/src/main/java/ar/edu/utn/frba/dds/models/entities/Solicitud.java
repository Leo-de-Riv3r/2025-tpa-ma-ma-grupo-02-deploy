package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.Estado;
import java.time.LocalDateTime;

import ar.edu.utn.frba.dds.models.enums.Motivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Solicitud {
  private Long id; //
  private String titulo;
  private String texto;
  private Motivo motivo; //
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
