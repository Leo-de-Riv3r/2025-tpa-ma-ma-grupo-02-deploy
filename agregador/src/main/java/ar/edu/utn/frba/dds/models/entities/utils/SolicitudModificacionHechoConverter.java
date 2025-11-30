package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.input.SolicitudModificacionHechoDto;
import ar.edu.utn.frba.dds.models.dtos.output.SolicitudModificacionHechoDtoOutput;
import ar.edu.utn.frba.dds.models.entities.SolicitudModificacionHecho;
import org.springframework.stereotype.Component;

@Component
public class SolicitudModificacionHechoConverter {
  public SolicitudModificacionHecho fromDto(SolicitudModificacionHechoDto solicitudModificacionHechoDto) {
    SolicitudModificacionHecho solicitudModificacionHecho = new SolicitudModificacionHecho();
    solicitudModificacionHecho.setDescripcion(solicitudModificacionHechoDto.getDescripcion());
    solicitudModificacionHecho.setLatitud(solicitudModificacionHechoDto.getLatitud());
    solicitudModificacionHecho.setLongitud(solicitudModificacionHechoDto.getLongitud());
    solicitudModificacionHecho.setCategoria(solicitudModificacionHechoDto.getCategoria());
    solicitudModificacionHecho.setCreador(solicitudModificacionHechoDto.getCreador());
    solicitudModificacionHecho.setFechaAcontecimiento(solicitudModificacionHechoDto.getFechaAcontecimiento());
    solicitudModificacionHecho.setTitulo(solicitudModificacionHechoDto.getTitulo());
    return solicitudModificacionHecho;
  }

  public SolicitudModificacionHechoDtoOutput fromEntity(SolicitudModificacionHecho solicitudModificacionHecho){
    SolicitudModificacionHechoDtoOutput solicitudModificacionHechoDtoOutput = new SolicitudModificacionHechoDtoOutput();
    solicitudModificacionHechoDtoOutput.setId(solicitudModificacionHecho.getId());
    solicitudModificacionHechoDtoOutput.setIdHecho(solicitudModificacionHecho.getId());
    solicitudModificacionHechoDtoOutput.setCreador(solicitudModificacionHecho.getCreador());
    solicitudModificacionHechoDtoOutput.setDescripcion(solicitudModificacionHecho.getDescripcion());
    solicitudModificacionHechoDtoOutput.setFechaAcontecimiento(solicitudModificacionHecho.getFechaAcontecimiento().toLocalDate().atStartOfDay());
    solicitudModificacionHechoDtoOutput.setLatitud(solicitudModificacionHecho.getLatitud());
    solicitudModificacionHechoDtoOutput.setLongitud(solicitudModificacionHecho.getLongitud());
    solicitudModificacionHechoDtoOutput.setTitulo(solicitudModificacionHecho.getTitulo());
    solicitudModificacionHechoDtoOutput.setCategoria(solicitudModificacionHecho.getCategoria());
    solicitudModificacionHechoDtoOutput.setFecha(solicitudModificacionHecho.getFecha());
    return solicitudModificacionHechoDtoOutput;
  }
}
