package ar.edu.utn.frba.dds.models.entities;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@Builder
public class Hecho {
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  @Builder.Default
  private Set<Etiqueta> etiquetas = Set.of();
  private Ubicacion ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private Origen origen;
  private Multimedia multimedia;
  @Builder.Default
  private Boolean eliminado = Boolean.FALSE;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Hecho hecho)) {
      return false;
    }
    return titulo.equals(hecho.titulo);
  }

  public boolean addEtiqueta(Etiqueta etiqueta) {
    return this.etiquetas.add(etiqueta);
  }
}
