package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTO;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Repository;


@Repository
public class ColeccionRepository implements IColeccionRepository {
  List<Coleccion> colecciones = List.of();

  @Override
  public Coleccion createColeccion(ColeccionDTO dto) {
    Coleccion coleccion = new Coleccion(dto.getTitulo(), dto.getDescripcion());
    colecciones.add(coleccion);
    return coleccion;
  }

  @Override
  public Coleccion updateColeccion(String coleccionId, ColeccionDTO dto) {
    Optional<Coleccion> coleccion = findById(coleccionId);
    if (coleccion.isPresent()) {
      Coleccion coleccionExistente = coleccion.get();
      if (dto.getTitulo() != null) {
        coleccionExistente.setTitulo(dto.getTitulo());
      }
      if (dto.getDescripcion() != null) {
        coleccionExistente.setDescripcion(dto.getDescripcion());
      }
      if (dto.getCriterios() != null) {
        coleccionExistente.setCriterios(dto.getCriterios());
      }
      if (dto.getFuentes() != null) {
        coleccionExistente.setFuentes(dto.getFuentes());
      }
      if (dto.getAlgoritmoConsenso() != null) {
        coleccionExistente.setAlgoritmoConsenso(dto.getAlgoritmoConsenso());
      }

      return coleccionExistente;
    } else {
      return null;
    }
  }

  @Override
  public Optional<Coleccion> findById(String coleccionId) {
    return colecciones.stream().filter(coleccion -> Objects.equals(coleccion.getId(), coleccionId)).findFirst();
  }

  @Override
  public List<Coleccion> getColecciones() {
    return colecciones;
  }

  @Override
  public Coleccion deleteColeccion(String coleccionId) {
    Optional<Coleccion> coleccion = findById(coleccionId);
    if (coleccion.isPresent()) {
      Coleccion coleccionEliminada = coleccion.get();
      colecciones.remove(coleccionEliminada);
      return coleccionEliminada;
    } else {
      return null;
    }
  }

  @Override
  public void refrescarHechosCurados() {
    colecciones.forEach(Coleccion::refrescarHechosCurados);
  }

  @Override
  public void addCriterio(String coleccionId, IFiltroStrategy criterio) {
    findById(coleccionId).ifPresent(coleccion -> coleccion.addCriterio(criterio));
  }

  @Override
  public void removeCriterio(String coleccionId, IFiltroStrategy criterio) {
    findById(coleccionId).ifPresent(coleccion -> coleccion.removeCriterio(criterio));
  }


  @Override
  public void addFuente(String coleccionId, Fuente fuente) {
    findById(coleccionId).ifPresent(coleccion -> coleccion.addFuente(fuente));
  }

  @Override
  public void removeFuente(String coleccionId, String fuenteId) {
    findById(coleccionId).ifPresent(coleccion -> coleccion.removeFuente(fuenteId));
  }
}
