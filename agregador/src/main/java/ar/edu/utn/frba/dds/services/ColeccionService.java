package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTO;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.impl.ColeccionRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ColeccionService {
  private final IColeccionRepository coleccionRepository;

  public ColeccionService(ColeccionRepository coleccionRepository) {
    this.coleccionRepository = coleccionRepository;
  }

  public Coleccion createColeccion(ColeccionDTOEntrada dto) {
    ColeccionDTO coleccion = new ColeccionDTO();
    coleccion.setTitulo(dto.getTitulo());
    coleccion.setDescripcion(dto.getDescripcion());
    return coleccionRepository.createColeccion(coleccion);
  }

  public List<Coleccion> getColecciones() {
    return coleccionRepository.getColecciones();
  }

  public Coleccion getColeccion(String coleccionId) {
    Optional<Coleccion> coleccion = coleccionRepository.findById(coleccionId);
    return coleccion.orElse(null);
  }

  public void updateColeccion(String coleccionId, ColeccionDTOEntrada dto) {
    ColeccionDTO coleccion = new ColeccionDTO();
    if (dto.getTitulo() != null) {
      coleccion.setTitulo(dto.getTitulo());
    }
    if (dto.getDescripcion() != null) {
      coleccion.setDescripcion(dto.getDescripcion());
    }
    if (dto.getFuentes() != null) {
      Set<Fuente> fuentes = new HashSet<>();
      dto.getFuentes().forEach(fuenteDTO -> {
        Fuente fuente = Fuente.convertirFuenteDTOAFuente(fuenteDTO);
        fuentes.add(fuente);
      });
      coleccion.setFuentes(fuentes);
    }
    if (dto.getAlgoritmo() != null) {
      TipoAlgoritmo tipoAlgoritmo = TipoAlgoritmo.valueOf(dto.getAlgoritmo());
      IConsensoStrategy algoritmoConsenso = tipoAlgoritmo.getStrategy();
      coleccion.setAlgoritmoConsenso(algoritmoConsenso);
    }
    coleccionRepository.updateColeccion(coleccionId, coleccion);
  }

  public void deleteColeccion(String coleccionId) {
    coleccionRepository.deleteColeccion(coleccionId);
  }

  public void refrescoColecciones() {
    this.getColecciones().forEach(coleccion -> coleccion.getFuentes().forEach(Fuente::refrescarHechos));
  }

  public Set<Hecho> getHechos(String coleccionId, boolean navegacionCurada, Integer page, Integer perPage) {
    Set<Hecho> hechos = Set.of();
    if (coleccionId != null) {
      Optional<Coleccion> coleccion = coleccionRepository.findById(coleccionId);
      if (navegacionCurada) {
        hechos = coleccion.map(Coleccion::getHechosCurados).orElse(null);
      } else {
        hechos = coleccion.map(Coleccion::getHechos).orElse(null);
      }
    }

    if (hechos == null || hechos.isEmpty()) {
      List<Coleccion> colecciones = this.getColecciones();
      for (Coleccion coleccion : colecciones) {
        Set<Hecho> hechosDeColeccion = navegacionCurada
            ? coleccion.getHechosCurados()
            : coleccion.getHechos();
        if (hechosDeColeccion != null && !hechosDeColeccion.isEmpty()) {
          if (hechos != null) {
            hechos.addAll(hechosDeColeccion);
          }
        }
      }
    }

    if (page != null && perPage != null) {
      return hechos != null ? hechos.stream()
          .skip((long) (page - 1) * perPage)
          .limit(perPage)
          .collect(HashSet::new, HashSet::add, HashSet::addAll) : null;
    }

    return hechos;
  }

  public void addFuente(String coleccionId, FuenteDTO dto) {
    Fuente fuente = Fuente.convertirFuenteDTOAFuente(dto);
    coleccionRepository.addFuente(coleccionId, fuente);
  }

  public void removeFuente(String coleccionId, String fuenteId) {
    coleccionRepository.removeFuente(coleccionId, fuenteId);
  }

  public void addCriterio(String coleccionId, IFiltroStrategy criterio) {
    coleccionRepository.addCriterio(coleccionId, criterio);
  }

  public void removeCriterio(String coleccionId, IFiltroStrategy criterio) {
    coleccionRepository.removeCriterio(coleccionId, criterio);
  }

  public void updateAlgoritmoConsenso(String coleccionId, TipoAlgoritmo algoritmoConsenso) {
    ColeccionDTOEntrada dto = new ColeccionDTOEntrada();
    dto.setAlgoritmo(algoritmoConsenso.name());
    updateColeccion(coleccionId, dto);
  }

  public void refrescarHechosCurados() {
    coleccionRepository.refrescarHechosCurados();
  }
}
