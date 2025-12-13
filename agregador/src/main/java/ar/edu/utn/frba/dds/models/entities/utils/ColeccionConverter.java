package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.FiltroDtoSalida;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroCategoria;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroDepartamento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFecha;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroMunicipio;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroProvincia;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ColeccionConverter {
  private final FuenteConverter fuenteConverter;
  public ColeccionConverter(FuenteConverter fuenteConverter) {
    this.fuenteConverter = fuenteConverter;
  }

  public ColeccionDTOSalida fromEntity(Coleccion coleccion) {
    ColeccionDTOSalida respuesta = new ColeccionDTOSalida();
    respuesta.setId(coleccion.getId());
    respuesta.setTitulo(coleccion.getTitulo());
    respuesta.setDescripcion(coleccion.getDescripcion());
    Set<Fuente> fuentes = coleccion.getFuentes();
    respuesta.setFuentes(fuentes.stream().map(fuenteConverter::fromEntity).toList());

    if(!coleccion.getCriterios().isEmpty()) {

      List<FiltroDtoSalida> criterioDtoList = new ArrayList<>();
      //List<CriterioColeccionDtoSalida> criterios = new ArrayList<>();
      coleccion.getCriterios().forEach(criterio -> {
        criterioDtoList.add(mapStrategyToDto(criterio));
      });
      respuesta.setCriterios(criterioDtoList);
    }
    if(coleccion.getAlgoritmoConsenso() != null) {
      respuesta.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso().getTipo().name());
    }
    return respuesta;
  }

  private FiltroDtoSalida mapStrategyToDto(IFiltroStrategy strategy) {
    FiltroDtoSalida dto = new FiltroDtoSalida();
    dto.setTipoFiltro(strategy.getTipoFiltro().toString());

    if (strategy instanceof FiltroCategoria f)
      dto.setValor(f.getNombreCategoria());
    else if (strategy instanceof FiltroProvincia f)
      dto.setValor(f.getProvincia());
    else if (strategy instanceof FiltroMunicipio f)
      dto.setValor(f.getMunicipio());
    else if (strategy instanceof FiltroDepartamento f)
      dto.setValor(f.getDepartamento());
    else if (strategy instanceof FiltroFuente f) {
      if (f.getTipoFuente() != null)
        dto.setTipoFuente(f.getTipoFuente().toString());
    } else if (strategy instanceof FiltroFecha f) {
      if (f.getFechaInicio() != null)
        dto.setFechaInicio(f.getFechaInicio().toLocalDate());
      if (f.getFechaFinal() != null)
        dto.setFechaFin(f.getFechaFinal().toLocalDate());
    }

    return dto;
  }
}
