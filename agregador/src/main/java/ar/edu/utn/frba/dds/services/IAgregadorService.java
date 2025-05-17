package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Solicitud;

public interface IAgregadorService {
  public void actualizarColecciones();
  public void createSolicitud(Solicitud solicitud) throws Exception;
}