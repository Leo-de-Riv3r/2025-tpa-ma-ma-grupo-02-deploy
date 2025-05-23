package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Solicitud {
  private String titulo;
  private String texto;
  private Hecho hecho;
  private Estado estadoActual;
  private LocalDateTime fecha;
  private String responsable;
  private String supervisor;
  private List<Estado> historial;
  public Solicitud(String titulo, String texto, Hecho hecho, String responsable) {
    this.titulo = titulo;
    this.texto = texto;
    this.hecho = hecho;
    this.estadoActual = new Estado("----", TipoEstado.PENDIENTE);
    this.fecha = LocalDateTime.now();
    this.responsable = responsable;
  }

  public Boolean estaFundado() {
    return texto.length() >= 500;
  }

  public void rechazar(String supervisor) {
    this.cambiarEstado(new Estado(supervisor, TipoEstado.RECHAZADA));
  }

  public void aceptar(String supervisor) {
    this.cambiarEstado(new Estado(supervisor, TipoEstado.ACEPTADA));
  }

  private void cambiarEstado(Estado estado) {
    this.historial.add(this.estadoActual);
    this.estadoActual = estado;
  }
}