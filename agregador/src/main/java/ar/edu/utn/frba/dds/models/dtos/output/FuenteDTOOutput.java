package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import lombok.Data;

@Data
public class FuenteDTOOutput {
  private String id;
  private String tipoFuente;
  private String url;
  public FuenteDTOOutput(String id, TipoFuente tipoFuente, String url) {
    this.id = id;
    this.tipoFuente = tipoFuente.toString();
    this.url = url;
  }
}
