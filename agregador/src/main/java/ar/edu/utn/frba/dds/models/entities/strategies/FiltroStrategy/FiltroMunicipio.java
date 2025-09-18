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
import lombok.Setter;

@Getter
@Entity @Table(name = "filtroMunicipio")
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
    return hecho.getUbicacion().getLugar().getMunicipio().equalsIgnoreCase(municipio);
  }
}
