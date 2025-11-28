package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.CriterioColeccionDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.CriterioDtoSalida;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroCategoria;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroDepartamento;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFecha;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroMunicipio;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.FiltroProvincia;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
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

      List<CriterioDtoSalida> criterioDtoList = new ArrayList<>();
      //List<CriterioColeccionDtoSalida> criterios = new ArrayList<>();
      coleccion.getCriterios().forEach(criterio -> {
        CriterioDtoSalida criterioDto = new CriterioDtoSalida();
        criterioDto.setTipoCriterio(criterio.getTipoFiltro().toString());
        if (criterio instanceof FiltroCategoria) {
          criterioDto.setValor(((FiltroCategoria) criterio).getNombreCategoria());
        }
        if (criterio instanceof FiltroProvincia) {
          criterioDto.setValor(((FiltroProvincia) criterio).getProvincia());
        }

        if (criterio instanceof FiltroDepartamento) {
          criterioDto.setValor(((FiltroDepartamento) criterio).getDepartamento());
        }

        if (criterio instanceof FiltroMunicipio) {
          criterioDto.setValor(((FiltroMunicipio) criterio).getMunicipio());
        }

        if (criterio instanceof FiltroFuente) {
          criterioDto.setTipoFuente(((FiltroFuente) criterio).getTipoFuente().toString());
        }

        if (criterio instanceof FiltroFecha) {
          criterioDto.setFechaInicio(LocalDate.from(((FiltroFecha) criterio).getFechaInicio()));
          criterioDto.setFechaFin(LocalDate.from(((FiltroFecha) criterio).getFechaFinal()));
        }
        criterioDtoList.add(criterioDto);
      });
      respuesta.setCriterios(criterioDtoList);
    }
    if(coleccion.getAlgoritmoConsenso() != null) {
      respuesta.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso().getClass().getSimpleName());
    }
    return respuesta;
  }
}
