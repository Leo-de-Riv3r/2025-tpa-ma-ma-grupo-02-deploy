package ar.edu.utn.frba.dds.ssr.utils;

import ar.edu.utn.frba.dds.ssr.models.dtos.input.ContribuyenteInputDTO;

public class ContribucionUtils {
    public static boolean tieneCredenciales(ContribuyenteInputDTO dto) {
        return dto.getEmail() != null && !dto.getEmail().isBlank() &&
                dto.getPassword() != null && !dto.getPassword().isBlank();
    }
}
