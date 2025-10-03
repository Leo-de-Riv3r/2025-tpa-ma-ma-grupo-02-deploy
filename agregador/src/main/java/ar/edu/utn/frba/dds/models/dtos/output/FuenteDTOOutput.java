package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import lombok.Data;

@Data
public class FuenteDTOOutput {
  private String id;
  private TipoFuente tipoFuente;
  private String url;
  private Integer cantidadHechos;
  public FuenteDTOOutput(String id, TipoFuente tipoFuente, String url, Integer cantidadHechos) {
    this.id = id;
    this.tipoFuente = tipoFuente;
    this.url = url;
    this.cantidadHechos = cantidadHechos;
  }
}
