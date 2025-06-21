package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;

public interface ISolicitudesService {
  void createSolicitud(Solicitud solicitud);

  void rechazarSolicitud(String id, String supervisor);

  void aceptarSolicitud(String id, String supervisor);

  Boolean hechoEliminado(Hecho hecho);
}