package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Getter
@Entity
@DiscriminatorValue("proxy")
@NoArgsConstructor
public class FuenteProxyMetamapa extends Fuente {
  public FuenteProxyMetamapa(String url) {
    super(url, TipoFuente.PROXY_METAMAPA);
  }

  @Override
  public Set<Hecho> obtenerHechosRefrescados(HechoConverter hechoConverter, WebClient webClient) {

    try {
      // 1. Obtener hechos desde la API externa

      Set<Hecho> hechos = webClient.get()
          .uri(url + "/hechos")
          .retrieve()
          .bodyToFlux(HechoDTOEntrada.class)
          .map(hecho -> hechoConverter.fromDTO(hecho, tipoFuente))
          .collect(Collectors.toSet())
          .block();

      // 2. Filtrar los hechos nuevos
      Set<Hecho> hechosFiltrados = hechos.stream()
          .filter(h -> !this.existeHecho(h))
          .collect(Collectors.toSet());

      // 3. Procesar ubicaciones en paralelo + usando cache
      return Flux.fromIterable(hechosFiltrados)
          .flatMap(h -> {

            Ubicacion ub = h.getUbicacion();

            return hechoConverter.obtenerLugarAsync(ub)
                .map(lugar -> {
                  ub.setLugar(lugar);
                  h.setUbicacion(ub);
                  return h;
                });
          })
          .collect(Collectors.toSet())
          .block();

    } catch (Exception e) {
      throw new RuntimeException("Error al obtener hechos de la fuente " + this.url);
    }
  }


  @Override
  public Set<Hecho> getHechos() {
    return hechos;
  }
}
