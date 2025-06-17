package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AlgoritmoConsenso {
  public int cantidadMinimaApariciones;

  public  Boolean cumpleConsenso(Hecho hecho, Set<IFuenteAdapter> fuentes, Set<FiltroStrategy> criterios) {
    //la fuente tendra que traer los hechos y ser capaz de convertirlos a clase hecho
    int cantApariciones = 0;
    int indice = 0;
    //discutir si al traer hechos trae todos paginados o no
    //HechosDTOEntrada hechosFuente = fuente.obtenerHechosUrl();
    for(IFuenteAdapter fuente : fuentes) {
      if(fuente.obtenerHechos(criterios).contains(hecho)) {
        cantApariciones++;
        if (cantApariciones == cantidadMinimaApariciones) {
          return true;
        }
      }
    }
    return false;
  }


  public Set<Hecho> obtenerHechosConsensuados(Set<IFuenteAdapter> fuentes, Set<FiltroStrategy> criterios) {
    Set<Hecho> hechos = new java.util.HashSet<>();
    fuentes.forEach(fuente -> {
      hechos.addAll(fuente.obtenerHechos(criterios).stream().filter(hecho -> cumpleConsenso(hecho, fuentes, criterios)).toList());
    }
    );
    return hechos;
  }
}
