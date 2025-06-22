package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AlgoritmoConsenso implements IAlgoritmoConsenso{
  public int cantidadMinimaApariciones;

  @Override
  public Hecho convertirHechoDTOAHechoComparable(Object hecho) {
    if (hecho instanceof Hecho) {
      return (Hecho) hecho;
    } else {
      //transformar hechos de fuente estatica o proxy
      HechosDTOEntrada hechoDto = (HechosDTOEntrada) hecho;
      Ubicacion ubicacion = new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud());
      Origen origen = new Origen(null, "---");
      return new Hecho(hechoDto.getTitulo(), hechoDto.getDescripcion(), hechoDto.getCategoria(), Set.of(), ubicacion, hechoDto.getFecha_hecho(), LocalDateTime.now(), origen, List.of());
    }
  }

  @Override
  public Boolean cumpleConsenso(Hecho hecho, Set<IFuenteAbstract> fuentes, Set<FiltroStrategy> criterios) {
    int cantApariciones = 0;
    FuenteResponseDTO hechosFuente;
  for(IFuenteAbstract fuente : fuentes) {
    int page = 1;
    do {
      hechosFuente = fuente.obtenerHechosUrl(page, 50);
      List<Hecho> hechos = hechosFuente.getHechosDTOEntrada().stream().map(hecho1 -> convertirHechoDTOAHechoComparable(hecho1))
          .filter(h -> fuente.cumpleCriterios(criterios, h)).toList();
      if (hechos.contains(hecho)) {
        cantApariciones++;
        if (cantApariciones == cantidadMinimaApariciones) {
          return true;
        }
      }
      page++;
    } while(!Objects.equals(hechosFuente.getCurrentPage(), hechosFuente.getLastPage()));
    }
    return false;
  }

  @Override
  public Set<Hecho> obtenerHechosConsensuados(Set<IFuenteAbstract> fuentes, Set<FiltroStrategy> criterios) {
    Set<Hecho> hechos = new java.util.HashSet<>();
    for (IFuenteAbstract fuente : fuentes) {
      FuenteResponseDTO responseFuente = fuente.obtenerHechosUrl();
      Integer paginaActual = 1;
      do {
        List<Object> hechosFuente = fuente.obtenerHechosUrl(paginaActual, 50).getHechosDTOEntrada();
        List<Hecho> hechos1 = hechosFuente.stream().map(this::convertirHechoDTOAHechoComparable)
            .filter(h -> cumpleConsenso(h, fuentes, criterios))
            .filter(h -> fuente.cumpleCriterios(criterios, h)).toList();
        hechos.addAll(hechos1);
        paginaActual++;
      } while (paginaActual <= responseFuente.getLastPage());
    }
    return hechos;
  }
}