package ar.edu.utn.frba.dds.ssr.mappers;

import ar.edu.utn.frba.dds.ssr.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import ar.edu.utn.frba.dds.ssr.models.entities.Multimedia;
import ar.edu.utn.frba.dds.ssr.models.enums.Formato;

public class HechoMapper {

    public static Hecho toEntity(HechoInputDTO hechoInputDTO) { // TODO: Revisar
        return Hecho.builder()
                .id(hechoInputDTO.getId())
                .titulo(hechoInputDTO.getTitulo())
                .descripcion(hechoInputDTO.getDescripcion())
                .multimedia(hechoInputDTO.getMultimedia().stream().map(m -> Multimedia.builder()
                                .nombre(m.getNombre())
                                .ruta(m.getRuta())
                                .formato(Formato.fromString(m.getFormato()))
                        .build()).toList())
                .build();
    }

    public static HechoOutputDTO toHechoOutputDTO(Hecho hecho) {
        return HechoOutputDTO.builder()
            // .id(hecho.getId())
            .titulo(hecho.getTitulo())
            .descripcion(hecho.getDescripcion())
            .categoria(hecho.getCategoria().getNombre())
            .latitud(hecho.getUbicacion().getLatitud())
            .longitud(hecho.getUbicacion().getLongitud())
            .fecha_hecho(hecho.getFechaAcontecimiento())
            .created_at(hecho.getFechaCarga())
            //.updated_at(hecho.getFechaCarga())
            /*.multimedia(
                hecho.getMultimedia().stream()
                    .map(m -> MultimediaOutputDTO.builder()
                        .nombre(m.getNombre())
                        .ruta(m.getRuta())
                        .formato(m.getFormato().name())
                        .build()
                    )
                    .toList()
            ) */
            .build();
    }
}
