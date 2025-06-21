package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;

import java.util.List;
import java.util.Set;

public interface IFuenteAbstract {
  Boolean cumpleCriterios(Set<FiltroStrategy> criterios, Hecho hecho);

  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios);

    public String getUrl();
    public void setHechos(List<Hecho> hechosActualizados);

    public TipoOrigen getTipoOrigen();
    public String getId();

    FuenteResponseDTO obtenerHechosUrl(Integer page, Integer per_page);

    FuenteResponseDTO obtenerHechosUrl();

  void actualizarHechos();

    Hecho convertirHechoDTOAHecho(Object hecho);
}
