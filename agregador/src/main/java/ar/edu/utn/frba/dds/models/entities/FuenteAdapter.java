package ar.edu.utn.frba.dds.models.entities;


import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.List;
import java.util.Set;

public class FuenteAdapter implements IFuenteAdapter{
  private String url;
  private TipoOrigen tipoOrigen;
  private List<Hecho> hechos;

  public FuenteAdapter(String url, TipoOrigen tipoOrigen) {
    this.url = url;
    this.tipoOrigen = tipoOrigen;
  }

  @Override
  public TipoOrigen getTipoOrigen() {
    return tipoOrigen;
  }
  private Boolean cumpleCriterios(Set<FiltroStrategy> criterios, Hecho hecho) {
    return criterios.stream().anyMatch(criterio -> !criterio.cumpleFiltro(hecho));
  }
  @Override
  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
    return (Set<Hecho>) hechos.stream().filter(hecho ->
      cumpleCriterios(criterios, hecho)).toList();
  }

  @Override
  public boolean eliminarHecho(Hecho hecho) {
    return false;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void setHechos(List<Hecho> hechosActualizados) {
    this.hechos = hechosActualizados;
  }

  @Override
  public boolean tiempoReal() {
    return this.tipoOrigen == TipoOrigen.PROXY;
  }
}
