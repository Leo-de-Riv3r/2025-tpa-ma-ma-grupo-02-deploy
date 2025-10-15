package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.EstadisticaDto;
import ar.edu.utn.frba.dds.models.NuevaEstadisticaDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EstadisticaService {
  private final MetamapaApiService metamapaApiService;

  public EstadisticaService(MetamapaApiService metamapaApiService) {
    this.metamapaApiService = metamapaApiService;
  }


  public void crearEstadistica(NuevaEstadisticaDto dto) {
    this.metamapaApiService.crearEstadistica(dto);
  }

  public List<EstadisticaDto> obtenerEstadisticas() {
    return this.metamapaApiService.obtenerEstadisticas();
  }
}
