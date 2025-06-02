package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.dtos.FuenteResponseDTO;
import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.IFuenteAdapter;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import ar.edu.utn.frba.dds.services.IColeccionesService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ColeccionesService implements IColeccionesService {
  private IColeccionesRepository coleccionesRepository;

  @Override
  public List<Coleccion> getColecciones(){
    return coleccionesRepository.getColecciones();
  }

  @Override
  public void refrescoColecciones() {
    List<Coleccion> colecciones = this.getColecciones();
    colecciones.forEach(coleccion -> actualizarHechosFuentes(coleccion));

    colecciones.forEach(coleccion -> setFuentesColeccion(coleccion.getId(), coleccion.getFuentes()));
  }

  @Override
  public void setFuentesColeccion(String handler, Set<IFuenteAdapter> fuentes) {
    this.coleccionesRepository.cambiarFuentesColeccion(handler, fuentes);
  }

  @Override
  public Hecho convertirHechoDTOAHecho(Object hecho, TipoOrigen tipoOrigen) {
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

  @Override
  public void actualizarHechosFuentes(Coleccion coleccion, Integer page, Integer per_page) {
    coleccion.getFuentes().forEach(fuente -> {
      if (!fuente.tiempoReal()) {
        List<Hecho> hechos = consultarHechos(fuente, page, per_page);
        fuente.setHechos(hechos);
      }
    });
  }

  @Override
  public void actualizarHechosFuentes(Coleccion coleccion) {
    coleccion.getFuentes().forEach(fuente -> {
      if (!fuente.tiempoReal()) {
        List<Hecho> hechosActualizados= consultarHechos(fuente);
        //reemplazar hechos de la fuente por los nuevos hechos
        fuente.setHechos(hechosActualizados);
      }
    });
  }

  //obtener todos los hechos
  @Override
  public List<Hecho> obtenerHechos() {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);

      hechos.addAll(obtenerHechosProxy(coleccion, null, null));
    });
    return hechos.stream().toList();
  }

  @Override
  public List<Hecho> obtenerHechos(Integer page, Integer per_page) {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      actualizarHechosFuentes(coleccion, page,per_page);
    });

    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);

      //tratamiento fuente proxy
      hechos.addAll(obtenerHechosProxy(coleccion, page, per_page));
    });

    return hechos.stream().toList();
  }

  @Override
  public List<Hecho> obtenerHechos(String handler) {
    Optional<Coleccion> coleccion = coleccionesRepository.findById(handler);
    List<Hecho> hechos = new ArrayList<>(List.of());
    if (coleccion.isPresent()) {
      Coleccion coleccion1 = coleccion.get();

      hechos.addAll(coleccion1.obtenerHechos());

      hechos.addAll(obtenerHechosProxy(coleccion1, null, null));

      return hechos.stream().toList();
    } else {
      return null;
    }
  }

  @Override
  public List<Hecho> obtenerHechos(String handler, Integer page, Integer per_page) {
    Optional<Coleccion> coleccion = coleccionesRepository.findById(handler);
    List<Hecho> hechos = List.of();
    if (coleccion.isPresent()) {
      Coleccion coleccion1 = coleccion.get();
      actualizarHechosFuentes(coleccion1, page, per_page);
      hechos.addAll(coleccion1.obtenerHechos());
      hechos.addAll(obtenerHechosProxy(coleccion1, page, per_page));
      return hechos.stream().toList();
    }
    else return null;
  }

  private List<Hecho> obtenerHechosProxy(Coleccion coleccion, Integer page, Integer per_page) {
    List<Hecho> hechos = new ArrayList<>(List.of());
    List<IFuenteAdapter> fuentesProxy = coleccion.getFuentes().stream()
        .filter(fuente -> fuente.tiempoReal())
        .toList();
    for (IFuenteAdapter IFuenteAdapter : fuentesProxy) {
      if (page != null && per_page != null) {
        hechos.addAll(consultarHechos(IFuenteAdapter));
      } else {
        hechos.addAll(consultarHechos(IFuenteAdapter, page, per_page));
      }
    }

    return hechos;
  }

  @Override
  public List<Hecho> consultarHechos(IFuenteAdapter fuente) {
    WebClient webClient = WebClient.builder().baseUrl(fuente.getUrl()).build();
    List<Object> hechosDTOEntrada = webClient
        .get()
        .uri("/hechos")
        .retrieve()
        .bodyToMono(FuenteResponseDTO.class)
        .map(FuenteResponseDTO::getHechosDTOEntrada).block();
    return hechosDTOEntrada.stream().map(hechoDto -> convertirHechoDTOAHecho(hechoDto, fuente.getTipoOrigen())).toList();
  }

  @Override
  public List<Hecho> consultarHechos(IFuenteAdapter fuente, Integer page, Integer per_page) {
    WebClient webClient =  WebClient.builder()
        .baseUrl(fuente.getUrl())
        .build();

    List<Object> hechosDTOEntrada = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/hechos")
            .queryParam("page", page)
            .queryParam("per_page", per_page)
            .build())
        .retrieve()
        .bodyToMono(FuenteResponseDTO.class)
        .map(FuenteResponseDTO::getHechosDTOEntrada).block();

    return hechosDTOEntrada.stream().map(hechoDto -> convertirHechoDTOAHecho(hechoDto, fuente.getTipoOrigen())).toList();
  }

  @Override
  public void agregarFuente(String handler, IFuenteAdapter fuente) {
    coleccionesRepository.agregarFuente(handler, fuente);
  }
}
