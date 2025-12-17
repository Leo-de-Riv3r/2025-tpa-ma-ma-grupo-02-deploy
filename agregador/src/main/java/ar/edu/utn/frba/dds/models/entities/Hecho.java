package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Objects;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "hecho", indexes = {
    @Index(name = "idx_hecho_eliminado", columnList = "eliminado") // Indice para velocidad
})
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "id_externo")
  private Long idExterno;
  @Column
  private String titulo;
  @Column(length = 3000)
  private String descripcion;
  @Column
  private String categoria;
  @Embedded
  private Ubicacion ubicacion;
  @Column
  private LocalDateTime fechaAcontecimiento;
  @Column
  private LocalDateTime fechaCarga;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "origen_id", referencedColumnName = "id")
  private Origen origen;

  @OneToMany(mappedBy = "hecho", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Multimedia> multimedia;

  @ManyToMany(mappedBy = "hechos", fetch = FetchType.LAZY)
  private Set<Fuente> fuentes;

  @ManyToMany(mappedBy = "hechosConsensuados", fetch = FetchType.LAZY)
  private Set<IConsensoStrategy> consensos;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean eliminado = false;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Hecho hecho))
      return false;

    return Objects.equals(titulo, hecho.titulo) &&
        Objects.equals(categoria, hecho.categoria) &&
        Objects.equals(descripcion, hecho.descripcion) &&
        (fechaAcontecimiento != null && hecho.fechaAcontecimiento != null &&
            fechaAcontecimiento.compareTo(hecho.fechaAcontecimiento) == 0)
        &&
        Objects.equals(ubicacion, hecho.ubicacion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(titulo, categoria, descripcion, fechaAcontecimiento, ubicacion);
  }

  public boolean cumpleFiltros(Set<IFiltroStrategy> filtros) {
    return filtros == null || filtros.isEmpty() || filtros.stream().allMatch(f -> f.cumpleFiltro(this));
  }
}