package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@Builder
public class Hecho {
  private String titulo;
  private String descripcion;
  private String categoria;
  @Builder.Default
  private Set<Etiqueta> etiquetas = Set.of();
  private Ubicacion ubicacion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private Origen origen;
  @Builder.Default
  private List<Multimedia> multimedia = List.of();

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

  public static Hecho convertirHechoDTOAHecho(HechoDTOEntrada dto, TipoFuente tipoFuente) {
    Ubicacion ubicacion = new Ubicacion(dto.getLatitud(), dto.getLongitud());
    Origen baseOrigin = new Origen(tipoFuente, null);
    return Hecho.builder()
        .titulo(dto.getTitulo())
        .descripcion(dto.getDescripcion())
        .categoria(dto.getCategoria())
        .ubicacion(ubicacion)
        .fechaAcontecimiento(dto.getFechaHecho())
        .fechaCarga(dto.getCreatedAt())
        .origen(baseOrigin)
        .build();
  }
}
