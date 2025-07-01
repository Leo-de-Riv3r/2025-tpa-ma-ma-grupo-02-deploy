package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class FuenteProxyMetamapa extends Fuente {
  public FuenteProxyMetamapa(String url) {
    super(url, TipoFuente.PROXY_METAMAPA);
  }

  @Override
  public void refrescarHechos() {
    // vacío, porque siempre se consulta en tiempo real
  }

  @Override
  public Set<Hecho> getHechos(Set<IFiltroStrategy> filtros) {
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/hechos").build())
        .retrieve()
        .bodyToFlux(HechoDTOEntrada.class)
        .map(hecho -> Hecho.convertirHechoDTOAHecho(hecho, tipoFuente))
        .filter(h -> cumpleFiltros(filtros, h))
        .collect(Collectors.toSet())
        .block();
  }
}