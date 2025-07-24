package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Hecho {
  private Long id;
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Set<Etiqueta> etiquetas;
  private Ubicacion ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private Origen origen;
  @Builder.Default
  private List<Multimedia> multimedia = new ArrayList<>(List.of());
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

  public void actualizarDesde(HechoInputDTO dto) {
    if (dto.getTitulo() != null) {
      titulo = dto.getTitulo();
    }
    if (dto.getDescripcion() != null) {
      descripcion = dto.getDescripcion();
    }
  }

  public void addMultimedia(Multimedia multimediaNueva) {
    multimedia.add(multimediaNueva);
  }
}
