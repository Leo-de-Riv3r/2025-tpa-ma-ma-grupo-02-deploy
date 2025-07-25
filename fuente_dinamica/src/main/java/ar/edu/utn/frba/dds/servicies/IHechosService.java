package ar.edu.utn.frba.dds.servicies;

import ar.edu.utn.frba.dds.models.dtos.input.SolicitudModificacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.List;

public interface IHechosService {
  public void modificarHecho(SolicitudModificacionInputDTO solicitudModificacion);
  public List<HechoOutputDTO> getHechos();
  public HechoOutputDTO getHechoById(Long id);
}
