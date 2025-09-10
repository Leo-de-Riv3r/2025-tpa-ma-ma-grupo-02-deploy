package ar.edu.utn.frba.dds.estadisticas.models.entities;

import ar.edu.utn.frba.dds.estadisticas.models.dto.input.ColeccionDTO;
import ar.edu.utn.frba.dds.estadisticas.models.dto.input.HechoDTO;
import ar.edu.utn.frba.dds.estadisticas.models.dto.input.LugarDTO;
import ar.edu.utn.frba.dds.estadisticas.models.dto.input.UbicacionDTO;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ConsultadorColeccion {
  public ColeccionDTO consultarLinkColeccion(String urlColeccion){
    WebClient webClient = WebClient.builder().baseUrl(urlColeccion).build();
    try {
      return webClient.get()
          .retrieve()
          .bodyToMono(ColeccionDTO.class)
          .block();
    } catch (Exception e) {
      throw new RuntimeException("No se pudo conectar coleccion de url " + urlColeccion);
    }
  }

  private Set<HechoDTO> consultarHechos(String urlColeccion) {
    WebClient webClient = WebClient.builder().baseUrl(urlColeccion).build();
    try {
      Set<HechoDTO> hechos = webClient.get()
          .uri(uriBuilder -> uriBuilder.path("/hechos").build())
          .retrieve()
          .bodyToFlux(HechoDTO.class)
          .collect(Collectors.toSet())
          .block();
      hechos.forEach(h -> {
        //manejo valores nulos de lugar
        LugarDTO lugar = new LugarDTO();
        if (h.getUbicacion().getLugar() != null) {
          lugar = h.getUbicacion().getLugar();
          if (lugar.getProvincia() == null) {
            lugar.setProvincia(" ");
          }
          if (lugar.getMunicipio() == null) {
            lugar.setMunicipio(" ");
          }
          if (lugar.getDepartamento() == null) {
            lugar.setDepartamento(" ");
          }
        }
        UbicacionDTO ubicacionDTO = h.getUbicacion();
        ubicacionDTO.setLugar(lugar);
        h.setUbicacion(ubicacionDTO);
      });
      return hechos;
    } catch (Exception e) {
      throw new RuntimeException("No se pudo conectar hechos de coleccion url " + urlColeccion);
    }
  }

  public Estadistica generarEstadistica(String urlColeccion, String categoriaEspecifica) {
    WebClient webClient = WebClient.builder().baseUrl(urlColeccion).build();

    ColeccionDTO coleccionDTO = this.consultarLinkColeccion(urlColeccion);
    Estadistica estadistica = new Estadistica();

    estadistica.setUrlColeccion(urlColeccion);
    estadistica.setNombre(coleccionDTO.getTitulo());
    estadistica.setCategoriaEspecifica(categoriaEspecifica);
    Set<HechoDTO> hechos = this.consultarHechos(urlColeccion);

    DetalleEstadistica detalles = this.calcularDetalles(estadistica);

//    private Number solcitudesSpam;
//  contar los hechos que esten marcados como spam
    detalles.setSolicitudesSpam(coleccionDTO.getCantSolicitudesSpam());
    estadistica.setDetalle(detalles);
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
                (categoriaBuscada.isEmpty() || conteoCategorias.get(categoria) > conteoCategorias.get(categoriaBuscada))
                    ? categoria
                    : categoriaBuscada
        );
    detalle.setCategoriaMayoresHechos(categoriaMayorCantHechos);
//    private String provinciaMayorHecho;
    Map<String, Long> conteoProvincias = hechos
        .stream()
        .filter(h -> h.getUbicacion().getLugar().getProvincia() != null)
        .collect(Collectors.groupingBy(hechoDTO -> hechoDTO.getUbicacion().getLugar().getProvincia(), Collectors.counting()));

    String provinciaMayorCantHechos = conteoProvincias.keySet().stream()
        .reduce("",
            (provinciaBuscada, provincia) ->
                (provinciaBuscada.isEmpty() || conteoProvincias.get(provincia) > conteoProvincias.getOrDefault(provinciaBuscada, 0L))
                    ? provincia
                    : provinciaBuscada
        );


    detalle.setProvinciaMayorCantHechos(provinciaMayorCantHechos);
//  ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
    if(estadistica.getCategoriaEspecifica() != null)
    {
      Map<String, Long> conteoProvinciasCategoria = hechos.stream()
        .filter(h -> Objects.equals(h.getCategoria(), estadistica.getCategoriaEspecifica()) && h.getUbicacion().getLugar().getProvincia() != null)
        .collect(Collectors.groupingBy(
            hecho -> hecho.getUbicacion().getLugar().getProvincia(),
            Collectors.counting()
        ));

      if (!conteoProvinciasCategoria.isEmpty()) {
        String provinciaMayorCantHechosCategoria = conteoProvincias.keySet().stream()
            .reduce("",
                (provinciaBuscada, provincia) ->
                    (provinciaBuscada.isEmpty() || conteoProvincias.get(provincia) > conteoCategorias.getOrDefault(provinciaBuscada, 0L))
                        ? provincia
                        : provinciaBuscada
            );
        detalle.setProvinciaMayorCantHechosCategoria(provinciaMayorCantHechosCategoria);
      }
  }
    //  ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
    Map<Integer, Long> conteoHorasCategoria = hechos.stream()
        .filter(h -> Objects.equals(h.getCategoria(), estadistica.getCategoriaEspecifica()))
        .collect(Collectors.groupingBy(
            hecho -> hecho.getFechaAcontecimiento().getHour(),
            Collectors.counting()
        ));
    Integer horaConMasHechos = conteoHorasCategoria.keySet().stream()
        .reduce((horaBuscada, hora) ->
            conteoHorasCategoria.get(hora) > conteoHorasCategoria.getOrDefault(horaBuscada, 0L)
                ? hora
                : horaBuscada
        ).orElse(null);
    detalle.setHoraMayorCantHechos(horaConMasHechos);

    ColeccionDTO coleccionDTO = this.consultarLinkColeccion(estadistica.getUrlColeccion());
    detalle.setSolicitudesSpam(coleccionDTO.getCantSolicitudesSpam());
    return detalle;
  }

}
