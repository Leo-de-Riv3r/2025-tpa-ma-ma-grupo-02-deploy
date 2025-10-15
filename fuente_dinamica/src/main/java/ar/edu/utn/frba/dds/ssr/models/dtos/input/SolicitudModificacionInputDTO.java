package ar.edu.utn.frba.dds.ssr.models.dtos.input;

import lombok.Data;

@Data
public class SolicitudModificacionInputDTO {
    private String titulo;
    private String texto;
    private ContribuyenteInputDTO contribuyente;
    private HechoInputDTO hecho;
}
