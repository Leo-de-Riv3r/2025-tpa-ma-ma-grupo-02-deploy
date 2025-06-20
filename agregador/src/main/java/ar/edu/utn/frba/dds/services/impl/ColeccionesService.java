package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.dtos.HechosDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.IFuenteAbstract;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import ar.edu.utn.frba.dds.models.repositories.ColecionesRepository;
import ar.edu.utn.frba.dds.models.repositories.IColeccionesRepository;
import ar.edu.utn.frba.dds.services.IColeccionesService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ColeccionesService implements IColeccionesService {
  private IColeccionesRepository coleccionesRepository;
  private Set<Hecho> hechosConsensuados;

  public ColeccionesService(ColecionesRepository colecionesRepository) {
    this.coleccionesRepository = colecionesRepository;
  }

  @Override
  public List<Coleccion> getColecciones(){
    return coleccionesRepository.getColecciones();
  }

  @Override
  public void refrescoColecciones() {
    this.getColecciones().forEach(coleccion -> actualizarHechosFuentes(coleccion));
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
  public void actualizarHechosFuentes(Coleccion coleccion) {
    coleccionesRepository.actualizarFuentes();
    }

  //obtener todos los hechos
  @Override
  public Set<Hecho> obtenerHechos() {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new ArrayList<>(List.of());
    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);
    });
    return new HashSet<>(hechos);
  }

  @Override
  public Set<Hecho> obtenerHechos(Integer page, Integer per_page) {
    List<Coleccion> colecciones = this.getColecciones();
    List<Hecho> hechos = new ArrayList<>(List.of());

    colecciones.forEach(coleccion -> {
      Set<Hecho> hechosColeccion = coleccion.obtenerHechos();
      hechos.addAll(hechosColeccion);
      //AGREGAR TRATAMIENTO PAGINACION
    });

    return new HashSet<>(hechos);
  }

  @Override
  public Set<Hecho> obtenerHechos(String handler) {
    Optional<Coleccion> coleccion = coleccionesRepository.findById(handler);
    List<Hecho> hechos = new ArrayList<>(List.of());
    if (coleccion.isPresent()) {
      return coleccion.get().obtenerHechos();
    } else {
      return null;
    }
  }

  @Override
  public Set<Hecho> obtenerHechos(String handler, Integer page, Integer per_page) {
    Optional<Coleccion> coleccion = coleccionesRepository.findById(handler);
    return coleccion.map(value -> value.obtenerHechos(page, per_page)).orElse(null);
  }

  @Override
  public void agregarFuente(String handler, IFuenteAbstract fuente) {
    coleccionesRepository.agregarFuente(handler, fuente);
  }

  @Override
  public void eliminarFuente(String handler, String idFuente) {
    coleccionesRepository.eliminarFuente(handler, idFuente);
  }

  @Override
  public void actualizarColeccion(String id, Coleccion coleccion) {
    coleccionesRepository.updateColeccion(id, coleccion);
  }

  @Override
  public void eliminarColeccion(String id) {
    coleccionesRepository.deleteColeccion(id);
  }

  @Override
  public void crearColeccion(Coleccion coleccion) {
    coleccionesRepository.createColeccion(coleccion);
  }

  @Override
  public void cambiarAlgoritmoConsenso(String id, AlgoritmoConsenso algoritmoConsenso) {
    coleccionesRepository.setAlgoritmoConsenso(id, algoritmoConsenso);
  }

  @Override
  public List<Hecho> obtenerHechosCurados(String id) {
    return coleccionesRepository.findById(id).get().getHechosConsensuados();
  }

  @Override
  public void actualizarHechosConsensuados() {
    coleccionesRepository.actualizarHechosConsensuados();
  }
}
