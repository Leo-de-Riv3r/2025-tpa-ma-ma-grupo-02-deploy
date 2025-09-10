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
@DiscriminatorValue("proxy")
@NoArgsConstructor
public class FuenteProxyMetamapa extends Fuente {
  public FuenteProxyMetamapa(String url) {
    super(url, TipoFuente.PROXY_METAMAPA);
  }

  @Override
  public void refrescarHechos() {
    // vacío, porque siempre se consulta en tiempo real
  }


  @Override
  public Set<Hecho> getHechos() {
    try {
      WebClient webClient = WebClient.builder().baseUrl(url).build();
      Set<Hecho> hechos = webClient.get()
          .uri(uriBuilder -> uriBuilder.path("/hechos").build())
          .retrieve()
          .bodyToFlux(HechoDTOEntrada.class)
          .map(hecho -> Hecho.convertirHechoDTOAHecho(hecho, tipoFuente))
          .collect(Collectors.toSet())
          .block();
      System.out.println("Hechos agregados: " + hechos.size());
      hechos.forEach(hecho -> {
        Lugar lugar = this.normalizadorLugar.obtenerLugar(hecho.getUbicacion());
        Ubicacion nuevaUbi = hecho.getUbicacion();
        nuevaUbi.setLugar(lugar);
        hecho.setUbicacion(nuevaUbi);
        System.out.println("Lugar asignado");
      });
      System.out.println("Lugares asignados exitosamente");
      return hechos;
    } catch (Exception e) {
      throw new RuntimeException("Error al tratar de obtener hechos de la fuente " + this.id);
    }
  }
}