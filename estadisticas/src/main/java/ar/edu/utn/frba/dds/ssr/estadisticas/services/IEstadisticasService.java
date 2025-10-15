package ar.edu.utn.frba.dds.ssr.estadisticas.services;

import ar.edu.utn.frba.dds.ssr.estadisticas.models.dto.input.EstadisticaNuevaDTO;
import ar.edu.utn.frba.dds.ssr.estadisticas.models.entities.Estadistica;
import java.util.List;

public interface IEstadisticasService {
  Estadistica createEstadistica(EstadisticaNuevaDTO dto);

  void actualizarEstadisticas();

  void eliminarEstadistica(Long id);

  List<Estadistica> getEstadisticas();

  Estadistica getEstadisticaById(Long estadisticaId);

  String exportarEstadisticasCSV(String rutaArchivo);

  void eliminarEstadisticasNoVigentes();
}
