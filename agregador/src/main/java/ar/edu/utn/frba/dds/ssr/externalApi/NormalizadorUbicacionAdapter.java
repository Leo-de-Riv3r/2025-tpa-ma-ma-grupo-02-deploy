package ar.edu.utn.frba.dds.ssr.externalApi;

import ar.edu.utn.frba.dds.ssr.models.entities.Lugar;
import ar.edu.utn.frba.dds.ssr.models.entities.Ubicacion;

public interface NormalizadorUbicacionAdapter {

  Lugar obtenerLugar(Ubicacion ubicacion);
}
