package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.services.SolicitudService;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class ColeccionConverter {
  private static SolicitudService solicitudService = null;

  public ColeccionConverter(SolicitudService solicitudService) {
    ColeccionConverter.solicitudService = solicitudService;
  }

  public static ColeccionDTOSalida fromEntity(Coleccion coleccion) {
    ColeccionDTOSalida respuesta = new ColeccionDTOSalida();
    respuesta.setId(coleccion.getId());
    respuesta.setTitulo(coleccion.getTitulo());
    respuesta.setDescripcion(coleccion.getDescripcion());
    Set<Fuente> fuentes = coleccion.getFuentes();
    respuesta.setFuentes(fuentes.stream().map(f -> new FuenteDTOOutput(f.getId(), f.getTipoFuente(), f.getUrl())).toList());
    respuesta.setCantSolicitudesSpam(obtenerCantSolicitudesSpam(coleccion.getHechos()));
    return respuesta;
  }

  private static Integer obtenerCantSolicitudesSpam(Set<Hecho> hechos) {
    AtomicReference<Integer> cantidadSolicitudesSpam = new AtomicReference<>(0);
    hechos .forEach(h -> {
      cantidadSolicitudesSpam.getAndSet(cantidadSolicitudesSpam.get() + solicitudService.cantidadSolicitudesSpam(h.getId()));
    });
    return cantidadSolicitudesSpam.get();
  }
}
