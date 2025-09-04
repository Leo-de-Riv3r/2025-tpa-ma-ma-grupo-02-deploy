package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import lombok.Data;

@Data
public class FuenteDTOOutput {
  private String id;
  private TipoFuente tipoFuente;
  private String url;

  public FuenteDTOOutput(String id, TipoFuente tipoFuente, String url) {
    this.id = id;
    this.tipoFuente = tipoFuente;
    this.url = url;
  }
}
