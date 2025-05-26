package ar.edu.utn.frba.dds.servicies.impl;

import ar.edu.utn.frba.dds.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.entities.Contribuyente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.IContribuyenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import java.time.LocalDateTime;

public class HechosService implements IHechosService {
  private IHechosRepository hechosRepository;
  private IContribuyenteRepository contribuyenteRepository;

  @Override
  public void editarHecho(ContribuyenteInputDTO contribuyenteInputDTO, HechoInputDTO hechoInputDTO) {
    Hecho hecho = hechosRepository.findById(hechoInputDTO.getId());

    if (puedeHechoSerEditadoPor(contribuyenteInputDTO, hecho)) {
      throw new RuntimeException("No puede editar el hecho");
    }

    hecho.actualizarBasadoEn(hechoInputDTO);
    hechosRepository.save(hecho);
  }

  private boolean tieneCredenciales(ContribuyenteInputDTO contribuyente) {
    String email = contribuyente.getEmail();
    String password = contribuyente.getPassword();
    return email != null && !email.isBlank() &&
        password != null && !password.isBlank();
  }

  private boolean puedeHechoSerEditadoPor(ContribuyenteInputDTO contribuyenteInputDTO, Hecho hecho) {
    LocalDateTime fechaCarga = hecho.getFechaCarga();
    Long idHecho = hecho.getId();
    if (!tieneCredenciales(contribuyenteInputDTO)) {
      return false;
    }
    Contribuyente contribuyente = contribuyenteRepository.findByEmail(contribuyenteInputDTO.getEmail());
    if (!contribuyente.getPassword().equals(contribuyenteInputDTO.getPassword())) {
      return false;
    }

    if (!contribuyente.tieneHecho(idHecho)) {
      return false;
    }

    LocalDateTime limite = fechaCarga.plusWeeks(1);//Posiblemente implementar patrón Strategy
    return LocalDateTime.now().isBefore(limite);
  }
}
