package ar.edu.utn.frba.dds.controllers; // O donde prefieras

import ar.edu.utn.frba.dds.models.dtos.input.ColeccionDTOEntrada;
import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.ColeccionDTOSalidaGQL;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDetallesDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.PaginacionDto;
import ar.edu.utn.frba.dds.models.dtos.output.HechoDtoSalida;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudResumenDtoOutput;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.SolicitudService;

import ar.edu.utn.frba.dds.models.dtos.input.graphql.HechosFiltroInput;

import ar.edu.utn.frba.dds.models.entities.factories.FiltroStrategyFactory;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.Argument;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AgregadorGQLController {
  private final SolicitudService solicitudService;
  private final ColeccionService coleccionService;

  public AgregadorGQLController(SolicitudService solicitudService, ColeccionService coleccionService) {
    this.solicitudService = solicitudService;
    this.coleccionService = coleccionService;
  }

  @QueryMapping

  public List<ColeccionDTOSalida> colecciones() {
    return coleccionService.getColeccionesDTO();
  }

  @QueryMapping
  public ColeccionDTOSalidaGQL coleccion(@Argument String id,
                                         @Argument Integer page,
                                         @Argument Boolean curados,
                                         @Argument HechosFiltroInput filtro) {
    Boolean curadosFinal = (curados == null) ? false : curados;
    Set<IFiltroStrategy> filtros = null;

    if (filtro != null) {
      filtros = FiltroStrategyFactory.fromParams(
          filtro.getCategoria(),
          (filtro.getFecha_acontecimiento_desde() != null) ? LocalDate.parse(filtro.getFecha_acontecimiento_desde()) : null,
          (filtro.getFecha_acontecimiento_hasta() != null) ? LocalDate.parse(filtro.getFecha_acontecimiento_hasta()) : null,
          filtro.getProvincia(),
          filtro.getMunicipio(),
          filtro.getDepartamento()
      );
    }
    return coleccionService.getColeccionOutputDto(id, curadosFinal, page, filtros);
  }

  @QueryMapping

  public HechoDetallesDtoSalida hecho(
      @Argument Long id
  ) {
    return coleccionService.getHechoDto(id);
  }


  //@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @QueryMapping
  public PaginacionDto<SolicitudResumenDtoOutput> solicitudes(
      @Argument Integer page,
      @Argument Boolean pendientes,
      @Argument Boolean filterByCreator
  ){
    return solicitudService.getSolicitudes(page, pendientes, filterByCreator);
  }

  //@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTRIBUYENTE')")
  @QueryMapping
  public SolicitudDTOOutput solicitud(
      @Argument Long id
  ){
    return solicitudService.getSolicitudDto(id);
  }

  //Mutations
  //TODO implementar en vista mutations
}