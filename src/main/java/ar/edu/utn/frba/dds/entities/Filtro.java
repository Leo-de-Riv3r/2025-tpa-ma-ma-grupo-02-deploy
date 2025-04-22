package ar.edu.utn.frba.dds.entities;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Filtro {
  private Categoria categoria;
  private Zona zona;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;

  public Boolean cumpleFiltro(Hecho hecho) {
    if (this.categoria != null && !this.categoria.equals(hecho.getCategoria())) {
      return false;
    }

    if (this.zona != null && !this.zona.pertenece(hecho.getUbicacion())) {
      return false;
    }

    if (this.fechaInicio != null && hecho.getFechaAcontecimiento().isBefore(this.fechaInicio.atStartOfDay())) {
      return false;
    }

    if (this.fechaFin != null && hecho.getFechaAcontecimiento().isAfter(this.fechaFin.atStartOfDay())) {
      return false;
    }

    return true;
  }
}
