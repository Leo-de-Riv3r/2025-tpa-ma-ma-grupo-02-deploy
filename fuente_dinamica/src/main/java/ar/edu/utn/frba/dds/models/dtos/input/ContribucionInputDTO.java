package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.Data;

@Data
public class ContribucionInputDTO {
    private HechoInputDTO hecho;
    private ContribuyenteInputDTO contribuyente;
}
