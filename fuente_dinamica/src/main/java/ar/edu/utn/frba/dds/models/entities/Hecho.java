package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "hechos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hecho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // TODO: ManyToMany??? Esto deberia tener su propia tabla?
    private Set<Etiqueta> etiquetas;

    @Embedded
    private Ubicacion ubicacion;

    @Column(name = "fecha_acontecimiento")
    private LocalDateTime fechaAcontecimiento;

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;

    // TODO: Esto deberia tener su propia tabla?
    private Origen origen;

    // TODO: OneToMany
    @Builder.Default
    private List<Multimedia> multimedia = new ArrayList<>(List.of());

    //
    @Builder.Default
    @Column(name = "eliminado")
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
