package ar.edu.utn.frba.dds.models.services.impl;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.repositories.HechosSolicitudesRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosSolicitudesRepository;
import ar.edu.utn.frba.dds.models.services.IAgregadorService;

public class AgregadorService implements IAgregadorService {
  private IHechosSolicitudesRepository hechosSolicitudesRepository = new HechosSolicitudesRepository();
  @Override
  public void actualizarColecciones() {

  }

  @Override
  public void createSolicitud(Solicitud solicitud) {

  }
}
