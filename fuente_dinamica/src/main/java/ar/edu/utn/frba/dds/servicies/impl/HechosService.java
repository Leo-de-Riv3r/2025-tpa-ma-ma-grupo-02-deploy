package ar.edu.utn.frba.dds.servicies.impl;

import ar.edu.utn.frba.dds.mappers.HechoMapper;
import ar.edu.utn.frba.dds.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Contribuyente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.IContribuyenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.servicies.IHechosService;
import ar.edu.utn.frba.dds.utils.ContribucionUtils;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class HechosService implements IHechosService {
  private IHechosRepository hechosRepository;
  private IContribuyenteRepository contribuyenteRepository;

  @Override
  public void modificarHecho(ContribuyenteInputDTO contribuyenteDTO, HechoInputDTO hechoDTO) {
    Contribuyente contribuyente = contribuyenteRepository.findByEmail(contribuyenteDTO.getEmail());

    if (!ContribucionUtils.tieneCredenciales(contribuyenteDTO)) {
      throw new RuntimeException("Se necesita ser contribuyente para editar un hecho");
    }

    if (!contribuyente.tieneHecho(hechoDTO.getId())) {
       throw new RuntimeException("No le pertenece el hecho al contribuyente");
    }

    Hecho hecho = hechosRepository.findById(hechoDTO.getId());

    if (!sePuedeEditarHecho(hecho)) {
      throw new RuntimeException("Paso la fecha limite para editar el hecho");
    }

    // Llamar a los repositorios de Categoria, Multimedia y Ubicación para actualizar los campos del input dto

    hecho.actualizarDesde(hechoDTO);

    hechosRepository.save(hecho);


  }

  private boolean sePuedeEditarHecho(Hecho hecho) {
    var fechaLimite = hecho.getFechaCarga().plusWeeks(1);
    return hecho.getFechaCarga().isBefore(fechaLimite);
  }

  public HechoPagDTO getHechos(Integer page, Integer per_page) {
    if (per_page < 1) per_page = 1;
    if (per_page > 100) per_page = 100;

    List<Hecho> hechos = hechosRepository.findAll();

    Integer total = hechos.size();
    Integer inicio = (page - 1) * per_page;
    Integer fin = Math.min(inicio + per_page, total);

    List<HechoOutputDTO> dtos = hechos
        .subList(0, 9)
        .stream()
        .map(HechoMapper::toHechoOutputDTO)
        .toList();

    return new HechoPagDTO(page, dtos);
  }
  @Override
  public HechoOutputDTO getHechoById(Long id) {
    Hecho hecho = hechosRepository.findById(id);
    if (hecho == null) {
      throw new RuntimeException("No existe ese hecho con ese id");
    }
    return HechoMapper.toHechoOutputDTO(hecho);
  }
}

