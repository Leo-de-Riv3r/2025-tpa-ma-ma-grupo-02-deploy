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

@Getter @Setter
@Entity
@Table(name = "filtroDepartamento")
@NoArgsConstructor
public class FiltroDepartamento extends IFiltroStrategy{
  @Column
  private String departamento;

  public FiltroDepartamento(String departamento) {
    if (departamento.isBlank()){
      throw new IllegalArgumentException("Provincia no puede ser nula");
    }
    this.departamento = departamento;
    this.tipoFiltro = TipoFiltro.FILTRO_DEPARTAMENTO;
  }
  @Override
  public Boolean cumpleFiltro(Hecho hecho){
    return hecho.getUbicacion().getLugar().getDepartamento().equalsIgnoreCase(departamento);
  }
}
