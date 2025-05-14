package ar.edu.utn.frba.dds.entities;

import ar.edu.utn.frba.dds.strategies.FiltroStrategy;

import java.util.Set;

public interface FuenteDeDatos {
    public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios);
    public boolean eliminarHecho(Hecho hecho);
}
