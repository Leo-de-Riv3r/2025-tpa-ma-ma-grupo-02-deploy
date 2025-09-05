package ar.edu.utn.frba.dds.estadisticas.models.entities;

import ar.edu.utn.frba.dds.estadisticas.models.dto.input.ColeccionDTO;
import ar.edu.utn.frba.dds.estadisticas.models.dto.input.HechoDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ConsultadorColeccion {
  public Estadistica generarEstadistica(String urlColeccion, String categoriaEspecifica) {
    WebClient webClient = WebClient.builder().baseUrl(urlColeccion).build();

    ColeccionDTO coleccionDTO = webClient.get()
        .retrieve()
        .bodyToMono(ColeccionDTO.class)
        .block();
    Estadistica estadistica = new Estadistica();

    estadistica.setUrlColeccion(urlColeccion);
    estadistica.setNombre(coleccionDTO.getTitulo());
    estadistica.setCategoriaEspecifica(categoriaEspecifica);
    Set<HechoDTO> hechos = this.consultarHechos(urlColeccion);

    DetalleEstadistica detalles = this.calcularDetalles(estadistica);
//    private Number solcitudesSpam;
//  contar los hechos que esten marcados como spam
    return estadistica;
  }

  public DetalleEstadistica calcularDetalles(Estadistica estadistica) {
    DetalleEstadistica detalle = new DetalleEstadistica();
    Set<HechoDTO> hechos = this.consultarHechos(estadistica.getUrlColeccion());
//    private String categoriaMayoresHechos;
    Map<String, Long> conteoCategorias = hechos.stream()
        .collect(Collectors.groupingBy(HechoDTO::getCategoria, Collectors.counting()));

    String categoriaMayorCantHechos = conteoCategorias.keySet().stream()
        .reduce("",
            (categoriaBuscada, categoria) ->
                (categoriaBuscada.equals("") || conteoCategorias.get(categoria) > conteoCategorias.get(categoriaBuscada))
                    ? categoria
                    : categoriaBuscada
        );
    detalle.setCategoriaMayoresHechos(categoriaMayorCantHechos);
//    private String provinciaMayorHecho;
    Map<String, Long> conteoProvincias = hechos.stream()
        .collect(Collectors.groupingBy(hechoDTO -> hechoDTO.getUbicacion().getLugar().getProvincia(), Collectors.counting()));

    String provinciaMayorCantHechos = conteoProvincias.keySet().stream()
        .reduce("",
            (provinciaBuscada, provincia) ->
                (provinciaBuscada.equals("") || conteoProvincias.get(provincia) > conteoCategorias.get(provinciaBuscada))
                    ? provincia
                    : provinciaBuscada
        );
    detalle.setProvinciaMayorCantHechos(provinciaMayorCantHechos);
//  ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    Map<String, Long> conteoProvinciasCategoria = hechos.stream()
        .filter(h -> h.getCategoria() == estadistica.getCategoriaEspecifica())
        .collect(Collectors.groupingBy(
            hecho -> hecho.getUbicacion().getLugar().getProvincia(),
            Collectors.counting()
        ));

    String provinciaMayorCantHechosCategoria = conteoProvincias.keySet().stream()
        .reduce("",
            (provinciaBuscada, provincia) ->
                (provinciaBuscada.equals("") || conteoProvincias.get(provincia) > conteoCategorias.get(provinciaBuscada))
                    ? provincia
                    : provinciaBuscada
        );
    detalle.setProvinciaMayorCantHechosCategoria(provinciaMayorCantHechosCategoria);
    //  ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
    Map<Integer, Long> conteoHorasCategoria = hechos.stream()
        .filter(h -> h.getCategoria() == estadistica.getCategoriaEspecifica())
        .collect(Collectors.groupingBy(
            hecho -> hecho.getFechaAcontecimiento().getHour(),
            Collectors.counting()
        ));

    Integer horaConMasHechos = conteoHorasCategoria.keySet().stream()
        .reduce(null,
            (horaBuscada, hora) ->
                (horaBuscada == null || conteoHorasCategoria.get(hora) > conteoHorasCategoria.get(horaBuscada))
                    ? hora
                    : horaBuscada
        );
    detalle.setHoraMayorCantHechos(horaConMasHechos);
    return detalle;
  }

  private Set<HechoDTO> consultarHechos(String urlColeccion) {
    WebClient webClient = WebClient.builder().baseUrl(urlColeccion).build();
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/hechos").build())
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collect(Collectors.toSet())
        .block();
  }
}
