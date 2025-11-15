package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity @Table(name = "filtroProvincia")
@NoArgsConstructor
public class FiltroProvincia extends IFiltroStrategy{
  @Column
  private String provincia;

  public FiltroProvincia(String provincia) {
    if (provincia.isBlank()){
      throw new IllegalArgumentException("Provincia no puede ser nula");
    }
    this.provincia = provincia;
    this.tipoFiltro = TipoFiltro.FILTRO_PROVINCIA;
  }
  @Override
  public Boolean cumpleFiltro(Hecho hecho) {
    if(hecho.getUbicacion().getLugar() != null && hecho.getUbicacion().getLugar().getProvincia() != null) return hecho.getUbicacion().getLugar().getProvincia().toLowerCase().contains(provincia);
    else return true;
  }

}
