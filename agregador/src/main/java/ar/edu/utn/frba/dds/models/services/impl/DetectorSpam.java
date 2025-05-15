package ar.edu.utn.frba.dds.models.services.impl;

import ar.edu.utn.frba.dds.models.services.IDetectorSpam;

public class DetectorSpam implements IDetectorSpam {

  @Override
  public boolean esSpam(String texto) {
    return false;
  }
}
