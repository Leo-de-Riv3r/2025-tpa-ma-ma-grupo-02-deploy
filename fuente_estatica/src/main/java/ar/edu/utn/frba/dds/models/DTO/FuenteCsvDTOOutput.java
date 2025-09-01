package ar.edu.utn.frba.dds.models.DTO;

import lombok.Data;
import lombok.Getter;

@Getter
public class FuenteCsvDTOOutput {
  private String link;
  private int cantidadHechos;

  public FuenteCsvDTOOutput(String link, int cantidadHechos) {
    this.link = link;
    this.cantidadHechos = cantidadHechos;
  }
}
