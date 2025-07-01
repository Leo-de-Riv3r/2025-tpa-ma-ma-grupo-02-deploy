package ar.edu.utn.frba.dds.servicies;

import ar.edu.utn.frba.dds.models.dtos.input.SolicitudModificacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;

public interface IHechosService {
  public void modificarHecho(SolicitudModificacionInputDTO solicitudModificacion);
  public HechoPagDTO getHechos(Integer page, Integer per_page);
  public HechoOutputDTO getHechoById(Long id);
}
