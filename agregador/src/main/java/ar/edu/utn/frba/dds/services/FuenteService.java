package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.FuenteNuevoDTO;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.FuenteDefault;
import ar.edu.utn.frba.dds.models.entities.FuenteProxyMetamapa;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FuenteService {
  private final IFuenteRepository fuenteRepository;
  private final IHechoRepository hechoRepository;
  public FuenteService(IFuenteRepository fuenteRepository, IHechoRepository hechoRepository) {
    this.fuenteRepository = fuenteRepository;
    this.hechoRepository = hechoRepository;
  }

  public List<FuenteDTOOutput> getFuentes() {
    List<Fuente> fuentes = fuenteRepository.findAll();
    return fuentes.stream().map(f -> new FuenteDTOOutput(f.getId(), f.getTipoFuente(), f.getUrl())).toList();
  }

  public FuenteDTOOutput createFuente(FuenteNuevoDTO fuenteDTO) {
    if (fuenteDTO.getTipoFuente() == null || fuenteDTO.getUrl() == null){
      throw new IllegalArgumentException("La url y/o el tipo de fuente estan vacios");
    } else {
      if (fuenteRepository.existsByUrl(fuenteDTO.getUrl())) {
        throw new EntityExistsException("Ya existe una fuente con la misma url");
      }
      try {
        TipoFuente tipoFuente = TipoFuente.valueOf(fuenteDTO.getTipoFuente().toUpperCase());
        Fuente fuente;
        if (tipoFuente == TipoFuente.PROXY_METAMAPA) {
          fuente = new FuenteProxyMetamapa(fuenteDTO.getUrl());
          //fuente.cambiarHechos(fuente.getHechos());
        } else if (tipoFuente == TipoFuente.DINAMICA || tipoFuente == TipoFuente.ESTATICA) {
          fuente = new FuenteDefault(fuenteDTO.getUrl(), tipoFuente);
          fuente.refrescarHechos();
        } else {
          throw new IllegalArgumentException("Tipo de fuente " + fuenteDTO.getTipoFuente() + " no soportado");
        }
        //guardo fuente con hechos ya filtrados(osea asegurandome de no tener hechos repetidos en la bd
        Fuente fuenteCreada = fuenteRepository.save(fuente);
        return new FuenteDTOOutput(fuente.getId(), fuenteCreada.getTipoFuente(), fuente.getUrl());
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Error inesperado");
      }
    }
  }
  public Fuente updateFuente() {
    //TODO
  return null;
  }

  public Fuente getFuente(String id) {
    return fuenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Fuente con id " + id + " no encontrada"));
  }

  @Transactional
  public void actualizarFuentes() {
    List<Fuente> fuentes = fuenteRepository.findAll();
    fuentes.forEach(Fuente::refrescarHechos);

    fuentes.forEach(fuente -> fuenteRepository.save(fuente));
    //aca podria hacer una op para eliminar hechos duplicados
  }

  public ResponseEntity<String> eliminarFuente(String idFuente) {
    Fuente fuente = this.getFuente(idFuente);
    if (fuente == null) {
      throw new IllegalArgumentException("Fuente con id " + idFuente + " no existente");
    }
    fuenteRepository.deleteById(idFuente);
    return ResponseEntity.ok("Operacion de eliminacion completada");
  }
}
