package ar.edu.utn.frba.dds.models.entities.factories;

import ar.edu.utn.frba.dds.models.dtos.FiltroDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroCategoria;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaAcontecimiento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFechaReporte;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroTitulo;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroUbicacion;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

  public static Set<IFiltroStrategy> fromParams(
      String categoria,
      LocalDateTime fechaReporteDesde,
      LocalDateTime fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde,
      LocalDateTime fechaAcontecimientoHasta,
      String ubicacion
  ) {
    Set<IFiltroStrategy> filtros = new HashSet<>();

    if (categoria != null)
      filtros.add(new FiltroCategoria(categoria));

    if (fechaAcontecimientoDesde != null || fechaAcontecimientoHasta != null)
      filtros.add(new FiltroFechaAcontecimiento(fechaAcontecimientoDesde, fechaAcontecimientoHasta));

    if (fechaReporteDesde != null || fechaReporteHasta != null)
      filtros.add(new FiltroFechaReporte(fechaReporteDesde, fechaReporteHasta));

    if (ubicacion != null && ubicacion.contains(",")) {
      try {
        String[] partes = ubicacion.split(",");
        Double lat = Double.parseDouble(partes[0].trim());
        Double lon = Double.parseDouble(partes[1].trim());
        filtros.add(new FiltroUbicacion(lat, lon));
      } catch (NumberFormatException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ubicación malformateada. Usar 'latitud,longitud'");
      }
    }

    return filtros;
  }
}