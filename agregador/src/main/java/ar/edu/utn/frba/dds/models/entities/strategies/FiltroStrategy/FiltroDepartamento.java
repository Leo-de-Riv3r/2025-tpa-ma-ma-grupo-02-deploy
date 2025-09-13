package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FiltroDepartamento extends IFiltroStrategy{
  @Column
  private String departamento;

  public FiltroDepartamento(String departamento) {
    this.departamento = departamento;this.tipoFiltro = TipoFiltro.FILTRO_DEPARTAMENTO;
  }
  @Override
  public Boolean cumpleFiltro(Hecho hecho){
    return hecho.getUbicacion().getLugar().getDepartamento().equalsIgnoreCase(departamento);
  }
}
