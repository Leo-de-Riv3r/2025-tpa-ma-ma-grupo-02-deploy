package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.externalApi.NormalizadorUbicacionAdapter;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
      this.hechos.clear();
      WebClient webClient = WebClient.builder().baseUrl(url).build();
      Set<Hecho> hechos = webClient.get()
          .uri(uriBuilder -> uriBuilder.path("/hechos").build())
          .retrieve()
          .bodyToFlux(HechoDTOEntrada.class)
          .map(hecho -> Hecho.convertirHechoDTOAHecho(hecho, tipoFuente))
          .collect(Collectors.toSet())
          .block();

      assert hechos != null;

      hechos.forEach(hecho -> {
        Lugar lugar = this.normalizadorLugar.obtenerLugar(hecho.getUbicacion());
        Ubicacion nuevaUbi = hecho.getUbicacion();
        nuevaUbi.setLugar(lugar);
        hecho.setUbicacion(nuevaUbi);
      });
      this.hechos.addAll(hechos);
    } catch (Exception e) {
      throw new RuntimeException("Error al tratar de obtener hechos de la fuente " + this.id);
    }
  }

  @Override
  public Set<Hecho> getHechos() {
    return hechos;
  }
}

