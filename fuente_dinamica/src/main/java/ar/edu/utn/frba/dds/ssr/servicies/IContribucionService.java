package ar.edu.utn.frba.dds.ssr.servicies;

import ar.edu.utn.frba.dds.ssr.models.dtos.input.ContribucionInputDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.ContribucionOutputDTO;

public interface IContribucionService {
    public ContribucionOutputDTO crearContribucion(ContribucionInputDTO contribucion);
}
