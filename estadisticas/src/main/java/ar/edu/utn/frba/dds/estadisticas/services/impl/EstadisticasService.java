package ar.edu.utn.frba.dds.estadisticas.services.impl;

import ar.edu.utn.frba.dds.estadisticas.models.dto.input.EstadisticaNuevaDTO;
import ar.edu.utn.frba.dds.estadisticas.models.entities.ConsultadorColeccion;
import ar.edu.utn.frba.dds.estadisticas.models.entities.DetalleEstadistica;
import ar.edu.utn.frba.dds.estadisticas.models.entities.Estadistica;
import ar.edu.utn.frba.dds.estadisticas.models.repositories.IRepositoryEstadisticas;
import ar.edu.utn.frba.dds.estadisticas.services.IEstadisticasService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EstadisticasService implements IEstadisticasService{
  private IRepositoryEstadisticas repositoryEstadisticas;
  private ConsultadorColeccion consultadorColeccion;
  public EstadisticasService(IRepositoryEstadisticas repositoryEstadisticas, ConsultadorColeccion consultadorColeccion) {
    this.repositoryEstadisticas = repositoryEstadisticas;
    this.consultadorColeccion = consultadorColeccion;
  }

  @Override
  public Estadistica createEstadistica(EstadisticaNuevaDTO dto) {
    Estadistica estadistica = consultadorColeccion.generarEstadistica(dto.getUrlColeccion(), dto.getCategoriaEspecifica());
    return repositoryEstadisticas.save(estadistica);
  }

  @Override
  @Transactional
  public void actualizarEstadisticas(){
    List<Estadistica> estadisticas = repositoryEstadisticas.findAll();
    estadisticas.forEach(estadistica -> {
      DetalleEstadistica detallesNuevos = consultadorColeccion.calcularDetalles(estadistica);
      estadistica.setDetalle(detallesNuevos);
    });
    repositoryEstadisticas.saveAll(estadisticas);
  }

  @Override
  @Transactional
  public void eliminarEstadistica(Long id) {
      repositoryEstadisticas.deleteById(id);
  }

  @Override
  public List<Estadistica> getEstadisticas() {
    return repositoryEstadisticas.findAll();
  }

  @Override
  public Estadistica getEstadisticaById(Long estadisticaId) {
    return repositoryEstadisticas.findById(estadisticaId).orElseThrow(
        () ->  new EntityNotFoundException("Estadistica con id " + estadisticaId + " no encontrada")
    );
    }
}
