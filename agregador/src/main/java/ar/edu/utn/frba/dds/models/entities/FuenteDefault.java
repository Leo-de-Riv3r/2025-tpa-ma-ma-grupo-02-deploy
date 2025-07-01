package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class FuenteDefault extends Fuente {
  public FuenteDefault(String url, TipoFuente tipoFuente) {
    super(url, tipoFuente);
  }

  @Override
  public void refrescarHechos() {
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    this.hechos = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/hechos").build())
        .retrieve()
        .bodyToFlux(HechoDTOEntrada.class)
        .map(hecho -> Hecho.convertirHechoDTOAHecho(hecho, tipoFuente))
        .collect(Collectors.toSet())
        .block();
  }

  @Override
  public Set<Hecho> getHechos(Set<IFiltroStrategy> filtros) {
    if (hechos.isEmpty()) {
      refrescarHechos();
    }
    return hechos.stream()
        .filter(h -> cumpleFiltros(filtros, h))
        .collect(Collectors.toSet());
  }
}

