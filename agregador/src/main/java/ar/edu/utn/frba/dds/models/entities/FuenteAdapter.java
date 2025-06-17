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

public class FuenteAdapter implements IFuenteAdapter{
  private String id;
  private String url;
  private TipoOrigen tipoOrigen;
  private List<Hecho> hechos;

  public FuenteAdapter(String url, TipoOrigen tipoOrigen) {
    this.id = UUID.randomUUID().toString().substring(0, 10);
    this.url = url;
    this.tipoOrigen = tipoOrigen;
  }

  @Override
  public TipoOrigen getTipoOrigen() {
    return tipoOrigen;
  }
  private Boolean cumpleCriterios(Set<FiltroStrategy> criterios, Hecho hecho) {
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
  public boolean tiempoReal() {
    return this.tipoOrigen == TipoOrigen.PROXY;
  }

  @Override
  public String getId() {
    return id;
  }

//  public List<Hecho> obtenerHechosUrl() {
//    WebClient webClient = WebClient.builder().baseUrl(url).build();
//    return List<Object> hechosDTOEntrada = webClient
//        .get()
//        .uri("/hechos")
//        .retrieve()
//        .bodyToMono(FuenteResponseDTO.class)
//        .map(FuenteResponseDTO::getHechosDTOEntrada).block();
//  }

//  public List<Hecho> obtenerHechosUrl(Integer page, Integer per_page) {
//    WebClient webClient = WebClient.builder().baseUrl(url).build();
//    return webClient.get()
//        .uri(uriBuilder -> uriBuilder
//            .path("/hechos")
//            .queryParam("page", page)
//            .queryParam("per_page", per_page)
//            .build())
//        .retrieve()
//        .bodyToMono(FuenteResponseDTO.class)
//        .map(FuenteResponseDTO::getHechosDTOEntrada).block();
//    //convetir hechos a dto?
//  }

  private Hecho convertirHechoDTOAHecho(Object hecho, TipoOrigen tipoOrigen) {
    if (hecho instanceof Hecho) {
      return (Hecho) hecho;
    } else {
      //transformar hechos de fuente estatica o proxy
      HechosDTOEntrada hechoDto = (HechosDTOEntrada) hecho;
      Ubicacion ubicacion = new Ubicacion(hechoDto.getLatitud(), hechoDto.getLongitud());
      Origen origen = new Origen(tipoOrigen, "---");
      return new Hecho(hechoDto.getTitulo(), hechoDto.getDescripcion(), hechoDto.getCategoria(), Set.of(), ubicacion, hechoDto.getFecha_hecho(), LocalDateTime.now(), origen, List.of());
    }
  }

}
