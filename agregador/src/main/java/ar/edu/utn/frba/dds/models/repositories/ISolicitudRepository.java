package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import java.util.List;

public interface ISolicitudRepository {
  void createSolicitud(Solicitud solicitud);

  void aceptarSolicitud(String id, String supervisor);

  void rechazarSolicitud(String id, String supervisor);

  boolean hechoEliminado(Hecho hecho);

  List<Solicitud> getSolicitudes();
}
