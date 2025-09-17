package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.externalApi.NormalizadorUbicacionAdapter;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Entity
@DiscriminatorValue("default")
@NoArgsConstructor
public class FuenteDefault extends Fuente {
  public FuenteDefault(String url, TipoFuente tipoFuente) {
    super(url, tipoFuente);
  }

  @Override
  public void refrescarHechos() {
    try {
      //this.hechos.clear();
      WebClient webClient = WebClient.builder().baseUrl(url).build();
      List<Hecho> hechos = webClient.get()
          .uri(uriBuilder -> uriBuilder.path("/hechos").build())
          .retrieve()
          .bodyToFlux(HechoDTOEntrada.class)
          .map(hecho -> HechoConverter.fromDTO(hecho, tipoFuente))
          .collect(Collectors.toList())
          .block();

      assert hechos != null;
      //solo agrego hechos nuevos segun titulo categoria y descripcion
      hechos = hechos.stream().filter(h -> !this.existeHecho(h)).toList();
      if (!hechos.isEmpty()) this.hechos.addAll(hechos);

      //normalizacion hechos

    } catch (Exception e) {
      throw new RuntimeException("Error al tratar de obtener hechos de la fuente " + this.id);
    }
  }

  @Override
  public Set<Hecho> getHechos() {
    return hechos;
  }
}

