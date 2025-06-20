package ar.edu.utn.frba.dds.models.dtos;

import ar.edu.utn.frba.dds.models.entities.AlgoritmoConsenso;
import lombok.Data;

@Data
public class CambiarConsensoDTOInput {
  private String id;
  private AlgoritmoConsenso algoritmoConsenso;
}
