package ar.edu.utn.frba.dds.models.entities;


import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class Fuente implements IFuenteAbstract {
  private String id;
  private String url;
  private TipoOrigen tipoOrigen;
  private List<Hecho> hechos;
  private IGeneradorIdAdapter generadorId = new GeneradorUUIIAdapter();

  public Fuente(String url, TipoOrigen tipoOrigen) {
    this.id = generadorId.generarId();
    this.url = url;
    this.tipoOrigen = tipoOrigen;
  }

  @Override
  public TipoOrigen getTipoOrigen() {
    return tipoOrigen;
  }

  @Override
  public Boolean cumpleCriterios(Set<FiltroStrategy> criterios, Hecho hecho) {
    return criterios.stream().anyMatch(criterio -> !criterio.cumpleFiltro(hecho));
  }
  @Override
  public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
    return (Set<Hecho>) hechos.stream().filter(hecho ->
      cumpleCriterios(criterios, hecho)).toList();
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void setHechos(List<Hecho> hechosActualizados) {
    this.hechos = hechosActualizados;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public FuenteResponseDTO obtenerHechosUrl(Integer page, Integer per_page) {
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/hechos")
            .queryParam("page", page)
            .queryParam("per_page", per_page)
            .build())
        .retrieve()
        .bodyToMono(FuenteResponseDTO.class)
        .block();
  }

  @Override
  public FuenteResponseDTO obtenerHechosUrl() {
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/hechos")
            .build())
        .retrieve()
        .bodyToMono(FuenteResponseDTO.class)
        .block();
  }

  @Override
  public void actualizarHechos() {
    hechos = obtenerHechosUrl().getHechosDTOEntrada().stream().map(this::convertirHechoDTOAHecho).toList();
  }

  @Override
  public Hecho convertirHechoDTOAHecho(Object hecho) {
    //transformar hechos de fuente estatica o proxy
    //agregar contemplacion de hecho de fuente dinamica(tiene multimedia y otras hierbas)
    HechosDTOEntrada hechoDto = (HechosDTOEntrada) hecho;
    Ubicacion ubicacion = new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud());
    Origen origen = new Origen(tipoOrigen, "---");
    return new Hecho(hechoDto.getTitulo(), hechoDto.getDescripcion(), hechoDto.getCategoria(), Set.of(), ubicacion, hechoDto.getFecha_hecho(), LocalDateTime.now(), origen, List.of());
  }
}
