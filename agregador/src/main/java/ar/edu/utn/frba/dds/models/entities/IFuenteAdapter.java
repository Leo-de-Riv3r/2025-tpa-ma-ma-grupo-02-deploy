package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import java.util.List;
import java.util.Set;

public interface IFuenteAdapter {
    public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios);

    public String getUrl();
    public void setHechos(List<Hecho> hechosActualizados);

    public boolean tiempoReal();
    public TipoOrigen getTipoOrigen();
    public String getId();
}
