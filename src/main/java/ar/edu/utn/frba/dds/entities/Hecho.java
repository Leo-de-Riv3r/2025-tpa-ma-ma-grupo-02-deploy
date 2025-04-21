package ar.edu.utn.frba.dds.entities;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hecho {
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Set<Etiqueta> etiquetas;
  private Ubicacion ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private Origen origen;
  private Multimedia multimedia;
  private Boolean eliminado;

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
}
