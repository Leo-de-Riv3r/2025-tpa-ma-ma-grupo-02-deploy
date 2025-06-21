package ar.edu.utn.frba.dds.models.entities;

import java.util.UUID;

public class GeneradorUUIIAdapter implements IGeneradorIdAdapter{

  @Override
  public String generarId() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
