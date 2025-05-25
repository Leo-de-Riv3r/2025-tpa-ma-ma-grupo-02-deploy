package ar.edu.utn.frba.dds.servicies.impl;

import ar.edu.utn.frba.dds.models.dtos.input.ContribucionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.models.entities.Contribuyente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.models.repositories.impl.ContribuyenteRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import java.nio.file.AccessDeniedException;

public class HechosService implements IHechosService {
  private IHechosRepository hechosRepository;
  private ContribuyenteRepository contribuyenteRepository;

  public void editarHecho(ContribucionInputDTO contribucionInputDTO) {
    Contribuyente contribuyente = contribuyenteRepository.findByEmail(contribucionInputDTO.getContribuyente().getEmail());
    Hecho hecho = hechosRepository.findById(contribucionInputDTO.getHecho().getId());

    if (!hecho.puedeSerEditadoPor(contribuyente)) {
      throw new RuntimeException("No puede editar el hecho");
    }

    hecho.actualizarBasadoEn(contribucionInputDTO.getHecho());
    hechosRepository.save(hecho);
  }
}
