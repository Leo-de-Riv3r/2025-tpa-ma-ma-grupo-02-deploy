package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;

public interface IHechosSolicitudesRepository {
  //TODO agregar entidades hecho sy solicitudes junto a las demas entidades necesarias

  public void createSolicitud(Solicitud solicitud);
  //TODO buscar forma de identificar solicitud por id, etc...
  public void aceptarSolicitud(Solicitud solicitud, String supervisor);
  public void rechazarSolicitud(Solicitud solicitud, String supervisor);

  boolean hechoEliminado(Hecho hecho);
}
