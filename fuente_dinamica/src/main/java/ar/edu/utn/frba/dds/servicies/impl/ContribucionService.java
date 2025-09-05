package ar.edu.utn.frba.dds.servicies.impl;

import ar.edu.utn.frba.dds.exceptions.ContrasenaIncorrectaException;
import ar.edu.utn.frba.dds.mappers.HechoMapper;
import ar.edu.utn.frba.dds.models.dtos.input.ContribucionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.ContribucionOutputDTO;
import ar.edu.utn.frba.dds.models.entities.Categoria;
import ar.edu.utn.frba.dds.models.entities.Contribuyente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.ICategoriaRepository;
import ar.edu.utn.frba.dds.models.repositories.IContribuyenteRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.servicies.IContribucionService;
import ar.edu.utn.frba.dds.utils.ContribucionUtils;
import org.springframework.stereotype.Service;

@Service
public class ContribucionService implements IContribucionService {
    private final IHechosRepository hechosRepository;
    private final IContribuyenteRepository contribuyenteRepository;
    private final ICategoriaRepository categoriaRepository;

    public ContribucionService(
            IHechosRepository hechosRepository,
            IContribuyenteRepository contribuyenteRepository,
            ICategoriaRepository categoriaRepository) {
        this.hechosRepository = hechosRepository;
        this.contribuyenteRepository = contribuyenteRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public ContribucionOutputDTO crearContribucion(ContribucionInputDTO contribucion) {
        var nombreCategoria = contribucion.getHecho().getCategoria();

        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria);

        if (categoria == null) {
            categoria = Categoria.builder()
                    .nombre(nombreCategoria)
                    .build();
            this.categoriaRepository.save(categoria);
        }

        Hecho hecho = HechoMapper.toEntity(contribucion.getHecho());

        hecho.setCategoria(categoria);

        hechosRepository.save(hecho);

        ContribuyenteInputDTO contribuyenteDto = contribucion.getContribuyente();

        if (ContribucionUtils.tieneCredenciales(contribuyenteDto)) {
            Contribuyente contribuyente = this.contribuyenteRepository.findByEmail(contribuyenteDto.getEmail());

            if (!contribuyente.getPassword().equals(contribuyenteDto.getPassword())) {
                throw new ContrasenaIncorrectaException();
            }

            contribuyente.agregarContribucion(hecho.getId());
            contribuyenteRepository.save(contribuyente);
        }

        return new ContribucionOutputDTO(); // TODO: Modelar esto
    }
}
