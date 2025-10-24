package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoUpdateDTO;
import ar.edu.utn.frba.dds.models.dtos.input.RevisionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoRevisionOutputDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IHechosService {
  //public void modificarHecho(SolicitudModificacionInputDTO solicitudModificacion);
  public List<HechoOutputDTO> getHechos();
  public HechoOutputDTO getHechoById(Long id);

  HechoOutputDTO crearHecho(HechoInputDTO hecho, List<MultipartFile> multimedia);
  HechoOutputDTO actualizarHecho(Long id, HechoUpdateDTO hechoDto, List<MultipartFile> multimedia, String username);

  // ADMINS
  public List<HechoRevisionOutputDTO> getHechosPendientes();
  public HechoRevisionOutputDTO aceptarHecho(Long id, RevisionInputDTO revisionDto);
  public HechoRevisionOutputDTO aceptarHechoConSugerencias(Long id, RevisionInputDTO revisionDto);
  public HechoRevisionOutputDTO rechazarHecho(Long id, RevisionInputDTO revisionDto);
}
