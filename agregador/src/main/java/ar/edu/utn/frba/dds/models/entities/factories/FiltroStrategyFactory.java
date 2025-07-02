package ar.edu.utn.frba.dds.models.entities.factories;

import ar.edu.utn.frba.dds.models.dtos.FiltroDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroTitulo;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroCategoria;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaAcontecimiento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaReporte;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroUbicacion;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;

public class FiltroStrategyFactory {
  public static IFiltroStrategy fromDTO(FiltroDTOEntrada dto) {
    return switch (dto.getTipoFiltro()) {
      case FILTRO_TITULO -> new FiltroTitulo(dto.getValor());
      case FILTRO_CATEGORIA -> new FiltroCategoria(dto.getValor());
      case FILTRO_FECHA_ACONTECIMIENTO_INICIO, FILTRO_FECHA_ACONTECIMIENTO_FIN ->
          new FiltroFechaAcontecimiento(dto.getFechaInicio(), dto.getFechaFin());
      case FILTRO_FECHA_REPORTE_INICIO, FILTRO_FECHA_REPORTE_FIN ->
          new FiltroFechaReporte(dto.getFechaInicio(), dto.getFechaFin());
      case FILTRO_UBICACION -> new FiltroUbicacion(dto.getLatitud(), dto.getLongitud());
    };
  }
}