package com.ddsi.utn.ba.ssr.services;

import com.ddsi.utn.ba.ssr.models.EstadisticaDto;
import com.ddsi.utn.ba.ssr.models.NuevaEstadisticaDto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
