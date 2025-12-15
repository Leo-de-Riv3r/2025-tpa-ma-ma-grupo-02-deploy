package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Getter
@Entity
@DiscriminatorValue("default")
@NoArgsConstructor
public class FuenteDefault extends Fuente {
  public FuenteDefault(String url, TipoFuente tipoFuente) {
    super(url, tipoFuente);
  }

  @Override
  public Set<Hecho> obtenerHechosRefrescados(HechoConverter hechoConverter, WebClient webClient) {
    try {
      Set<Hecho> hechos = webClient.get()
          .uri(url + "/hechos")
          .retrieve()
          .bodyToFlux(HechoDTOEntrada.class)
          .map(hecho -> hechoConverter.fromDTO(hecho, tipoFuente))
          .collect(Collectors.toSet())
          .block();

      hechos.forEach((h -> {
        Ubicacion ubicacionNueva = h.getUbicacion();
        ubicacionNueva.setLugar(hechoConverter.obtenerLugar(ubicacionNueva));
        h.setUbicacion(ubicacionNueva);
      }));

      if (tipoFuente == TipoFuente.ESTATICA && this.hechos.size() >= 10000) {
        return new HashSet<>();
      } else {
        return hechos;
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al tratar de obtener hechos de la fuente " + this.getUrl());
    }
  }


  @Override
  public Set<Hecho> getHechos() {

    return hechos.stream()
        .sorted(Comparator.comparing(Hecho::getId))
        .collect(Collectors.toSet());
  }
}

