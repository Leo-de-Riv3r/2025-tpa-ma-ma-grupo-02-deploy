package ar.edu.utn.frba.dds.ssr.servicies;

import ar.edu.utn.frba.dds.ssr.models.dtos.input.RespuestaSolicitudInputDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.input.SolicitudEliminacionInputDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.SolicitudEliminacionOutputDTO;
import java.util.List;

public interface ISolicitudesService {
  public List<SolicitudEliminacionOutputDTO> getSolicitudesPendientes();
  public SolicitudEliminacionOutputDTO crearSolicitudEliminacion(SolicitudEliminacionInputDTO solicitudDto);
  public SolicitudEliminacionOutputDTO aceptarSolicitud(Long solicitudId, RespuestaSolicitudInputDTO respuestaDto);
  public SolicitudEliminacionOutputDTO rechazarSolicitud(Long solicitudId, RespuestaSolicitudInputDTO respuestaDto);
}
