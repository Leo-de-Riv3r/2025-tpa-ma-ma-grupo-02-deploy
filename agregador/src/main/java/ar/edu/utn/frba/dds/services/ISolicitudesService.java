package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.IFuenteAdapter;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import java.util.List;
import java.util.Set;

public interface ISolicitudesService {
  void createSolicitud(Solicitud solicitud);

  void rechazarSolicitud(Solicitud solicitud, String supervisor);

  void aceptarSolicitud(Solicitud solicitud, String supervisor);
}