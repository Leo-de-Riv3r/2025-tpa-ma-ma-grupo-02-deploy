package ar.edu.utn.frba.dds.ssr.models.dtos.input;

import lombok.Data;

@Data
public class ContribuyenteInputDTO {
    private String nombre; // TODO: Nombre obligatorio, agregar excepción!
    private String email;
    private String password;
}
