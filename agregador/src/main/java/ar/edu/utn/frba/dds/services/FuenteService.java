package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.FuenteNuevoDTO;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.FuenteDefault;
import ar.edu.utn.frba.dds.models.entities.FuenteProxyMetamapa;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.repositories.IColeccionRepository;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FuenteService {
  private final IFuenteRepository fuenteRepository;
  public FuenteService(IFuenteRepository fuenteRepository) {
    this.fuenteRepository = fuenteRepository;
  }

  public List<Fuente> getFuentes() {
    return fuenteRepository.findAll();
  }

  public Fuente createFuente(FuenteNuevoDTO fuenteDTO) {
    if (fuenteDTO.getTipoFuente() == null || fuenteDTO.getUrl() == null){
      throw new IllegalArgumentException("La url y/o el tipo de fuente estan vacios");
    } else {
      try {
        TipoFuente tipoFuente = TipoFuente.valueOf(fuenteDTO.getTipoFuente().toUpperCase());
        Fuente fuente;
        if (tipoFuente == TipoFuente.PROXY_METAMAPA) {
          fuente = new FuenteProxyMetamapa(fuenteDTO.getUrl());
        } else if (tipoFuente == TipoFuente.DINAMICA || tipoFuente == TipoFuente.ESTATICA) {
          fuente = new FuenteDefault(fuenteDTO.getUrl(), tipoFuente);
        } else {
          throw new IllegalArgumentException("Tipo de fuente " + fuenteDTO.getTipoFuente() + " no soportado");
        }
        //limpio los hechos
        System.out.println("GUARDANDO GFUENTE");
        return fuenteRepository.save(fuente);
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
    return fuenteRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Fuente con id " + id + " no encontrada"));
  }

  @Transactional
  public void actualizarFuentes() {
    List<Fuente> fuentes = this.getFuentes();
    fuentes.forEach(Fuente::refrescarHechos);
    fuentes.forEach(fuente -> fuenteRepository.save(fuente));
  }

  public Fuente eliminarFuente(String idFuente) {
    Fuente fuente = this.getFuente(idFuente);
    fuenteRepository.deleteById(idFuente);
    return fuente;
  }
}
