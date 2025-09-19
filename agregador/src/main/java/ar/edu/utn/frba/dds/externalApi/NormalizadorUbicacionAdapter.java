package ar.edu.utn.frba.dds.externalApi;

import ar.edu.utn.frba.dds.models.entities.Lugar;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;

public interface NormalizadorUbicacionAdapter {

  Lugar obtenerLugar(Ubicacion ubicacion);
}
