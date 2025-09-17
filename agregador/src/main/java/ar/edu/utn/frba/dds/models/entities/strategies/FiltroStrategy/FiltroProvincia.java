package ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFiltro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Timer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity @Table(name = "filtroProvincia")
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
    return hecho.getUbicacion().getLugar().getProvincia().equalsIgnoreCase(provincia);
  }

}
