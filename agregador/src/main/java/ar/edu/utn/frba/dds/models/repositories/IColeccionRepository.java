package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTO;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.List;
import java.util.Optional;

public interface IColeccionRepository {
  Coleccion createColeccion(ColeccionDTO dto);
  List<Coleccion> getColecciones();
  Optional<Coleccion> findById(String coleccionId);
  Coleccion updateColeccion(String coleccionId, ColeccionDTO dto);
  Coleccion deleteColeccion(String coleccionId);

  void refrescarHechosCurados();

  void addCriterio(String coleccionId, IFiltroStrategy criterio);
  void removeCriterio(String coleccionId, IFiltroStrategy criterio);

  void addFuente(String coleccionId, Fuente fuente);
  void removeFuente(String coleccionId, String fuenteId);
}
