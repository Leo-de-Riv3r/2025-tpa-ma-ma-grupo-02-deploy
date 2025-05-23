package ar.edu.utn.frba.dds.models.dtos.input;

import lombok.Data;

@Data
public class ContribuidorInputDTO {
    private String email;
    private String password;

    public boolean tieneCredenciales() {
        return email != null && !email.isBlank() &&
                password != null && !password.isBlank();
    }
}
