package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name="filtroTitulo")
@NoArgsConstructor
public class FiltroTitulo extends IFiltroStrategy {
  @Column
  private String titulo;

  public FiltroTitulo (String titulo) {
    if (titulo.isBlank()) {
      throw  new IllegalArgumentException("Titulo no puede ser nulo");
    }
    this.titulo = titulo;
    this.tipoFiltro = TipoFiltro.FILTRO_TITULO;
  }
  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    return hecho.getTitulo().equalsIgnoreCase(titulo);
  }
}
