package ar.edu.utn.frba.dds.servicies;

import ar.edu.utn.frba.dds.models.dtos.input.ContribucionInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.ContribucionOutputDTO;

public interface IContribucionService {
    public ContribucionOutputDTO crearContribucion(ContribucionInputDTO contribucion);
}
