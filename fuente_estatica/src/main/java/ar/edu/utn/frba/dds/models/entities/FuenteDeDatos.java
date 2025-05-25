package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import java.util.Set;

public interface FuenteDeDatos {
    public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios);
    public boolean eliminarHecho(Hecho hecho);
}
