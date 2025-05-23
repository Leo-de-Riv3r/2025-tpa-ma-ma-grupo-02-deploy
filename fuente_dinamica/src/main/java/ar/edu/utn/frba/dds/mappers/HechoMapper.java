package ar.edu.utn.frba.dds.mappers;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;

public class HechoMapper {

    public static Hecho toEntity(HechoInputDTO hechoInputDTO) { // TODO: Revisar
        return Hecho.builder()
                .id(hechoInputDTO.getId())
                .titulo(hechoInputDTO.getTitulo())
                .descripcion(hechoInputDTO.getDescripcion())
                .build();
    }

}
