package ar.edu.utn.frba.dds.servicies;

import ar.edu.utn.frba.dds.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;

public interface IHechosService {
  public void editarHecho(ContribuyenteInputDTO contribuyenteInputDTO, HechoInputDTO hechoInputDTO);
}
