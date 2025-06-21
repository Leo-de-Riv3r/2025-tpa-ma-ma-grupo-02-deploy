package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.reactive.function.client.WebClient;

public class FuenteProxy extends Fuente{
  public FuenteProxy(String url) {
    super(url, TipoOrigen.PROXY);
  }

  @Override
  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
    List<Hecho> hechos;
    hechos = obtenerHechosUrl().getHechosDTOEntrada().stream().map(this::convertirHechoDTOAHecho).toList();

    return hechos.stream().filter(hecho -> cumpleCriterios(criterios, hecho)).collect(Collectors.toSet());
  }

  @Override
  public void actualizarHechos() {}
}