package ar.edu.utn.frba.dds.ssr.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import ar.edu.utn.frba.dds.ssr.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity @Table(name = "filtroMunicipio")
@NoArgsConstructor
public class FiltroMunicipio extends IFiltroStrategy{
  @Column
  private String municipio;


  public FiltroMunicipio(String municipio) {
    if (municipio.isBlank()){
      throw new IllegalArgumentException("Municipio no puede ser nula");
    }
    this.tipoFiltro = TipoFiltro.FILTRO_MUNICIPIO;
    this.municipio = municipio;
  }

  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    return hecho.getUbicacion().getLugar().getMunicipio().toLowerCase().contains(municipio);
  }
}
