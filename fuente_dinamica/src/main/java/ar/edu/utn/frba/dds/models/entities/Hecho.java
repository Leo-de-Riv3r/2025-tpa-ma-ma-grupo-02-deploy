package ar.edu.utn.frba.dds.models.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Hecho {
  private Long id; //ver
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Set<Etiqueta> etiquetas;
  private Ubicacion ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private Origen origen;
  private List<Multimedia> multimedia;
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

  public boolean agregar(Etiqueta etiqueta) {
    return this.etiquetas.add(etiqueta);
  }

}
