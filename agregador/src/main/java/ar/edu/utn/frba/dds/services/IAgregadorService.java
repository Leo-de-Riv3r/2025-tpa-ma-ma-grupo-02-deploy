package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Solicitud;
import java.util.List;
import java.util.Set;

public interface IAgregadorService {
  public void createSolicitud(Solicitud solicitud);
  public void rechazarSolicitud(Solicitud solicitud, String supervisor);
  public List<Coleccion> getColecciones();
  public void refrescoColecciones();
  public void setFuentesColeccion(String handler, Set<FuenteDeDatos> fuentes);

  void actualizarColeccion(String handler, Coleccion coleccion);

  void actualizarHechosFuentes(Coleccion coleccion);

  Hecho convertirHechoDTOAHecho(Object hecho);

  public List<Hecho> obtenerHechos();


  List<HechosDTOEntrada> consultarHechos(String url);

  public void agregarFuente(String handler, FuenteDeDatos fuente);



}