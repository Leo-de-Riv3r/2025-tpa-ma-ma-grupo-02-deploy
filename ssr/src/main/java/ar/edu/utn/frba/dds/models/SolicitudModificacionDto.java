package ar.edu.utn.frba.dds.models;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class SolicitudModificacionDto {
  private Long idHecho;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String fechaAcontecimiento;
  private String creador;

  public Long getIdHecho() {
    return idHecho;
  }

  public void setIdHecho(Long idHecho) {
    this.idHecho = idHecho;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public Double getLatitud() {
    return latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  public String getFechaAcontecimiento() {
    return fechaAcontecimiento;
  }

  public void setFechaAcontecimiento(String fechaAcontecimiento) {
    this.fechaAcontecimiento = fechaAcontecimiento;
  }

  public String getCreador() {
    return creador;
  }

  public void setCreador(String creador) {
    this.creador = creador;
  }
}
