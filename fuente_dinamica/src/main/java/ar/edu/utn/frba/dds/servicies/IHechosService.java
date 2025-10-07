package ar.edu.utn.frba.dds.servicies;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.SolicitudModificacionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoRevisionOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import org.springframework.data.history.Revision;

import java.util.List;

public interface IHechosService {
  //public void modificarHecho(SolicitudModificacionInputDTO solicitudModificacion);
  public List<HechoOutputDTO> getHechos();
  public HechoOutputDTO getHechoById(Long id);

  HechoOutputDTO crearHecho(HechoInputDTO hecho);

  // ADMINS
  public List<HechoRevisionOutputDTO> getHechosPendientes();
  public HechoRevisionOutputDTO aceptarHecho(Long id, RevisionInputDTO revisionDto);
  public HechoRevisionOutputDTO aceptarHechoConSugerencias(Long id, RevisionInputDTO revisionDto);
  public HechoRevisionOutputDTO rechazarHecho(Long id, RevisionInputDTO revisionDto);
}
