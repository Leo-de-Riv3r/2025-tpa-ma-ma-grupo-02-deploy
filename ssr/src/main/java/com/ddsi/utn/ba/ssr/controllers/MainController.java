package com.ddsi.utn.ba.ssr.controllers;
import com.ddsi.utn.ba.ssr.models.Coleccion;
import com.ddsi.utn.ba.ssr.models.ColeccionDetallesDto;
import com.ddsi.utn.ba.ssr.models.ColeccionNuevaDto;
import com.ddsi.utn.ba.ssr.models.FiltrosDto;
import com.ddsi.utn.ba.ssr.models.FuenteNuevaDto;
import com.ddsi.utn.ba.ssr.models.HechoDetallesDto;
import com.ddsi.utn.ba.ssr.models.HechoDto;
import com.ddsi.utn.ba.ssr.models.ResumenActividadDto;
import com.ddsi.utn.ba.ssr.models.SolicitudEliminacionDetallesDto;
import com.ddsi.utn.ba.ssr.models.SolicitudEliminacionDto;
import com.ddsi.utn.ba.ssr.models.SolicitudesPaginasDto;
import com.ddsi.utn.ba.ssr.services.AgregadorService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
  private final AgregadorService agregadorService;

  public MainController(AgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }

  @GetMapping("/")
  public String home() {
    return "home";  }

  @PostMapping("/solicitarEliminacion")
  public String procesarSolicitudEliminacion(@ModelAttribute("solicitudEliminacion") SolicitudEliminacionDto solicitud) {
    //envio solicitud
    agregadorService.enviarSolicitud(solicitud);
    return "redirect:/colecciones";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos/{idHecho}/solicitudEliminacion")
  public String mostrarFormularioSolicitud(
      @PathVariable String idColeccion, @PathVariable Long idHecho, Model model
  ){
    SolicitudEliminacionDto solicitud = new SolicitudEliminacionDto();
    solicitud.setIdHecho(idHecho);
    model.addAttribute("solicitudEliminacion", solicitud);
    model.addAttribute("hechoId", idHecho);
    return "coleccion/solicitudEliminacion";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos/{idHecho}")
  public String getDetallesHecho(@PathVariable Long idHecho, @PathVariable String idColeccion, Model model) {
    HechoDetallesDto hechoDetallesDto = agregadorService.getDetallesHecho(idHecho);
    model.addAttribute("idColeccion", idColeccion);
    model.addAttribute("hecho", hechoDetallesDto);
    return "coleccion/detallesHecho";
  }

@GetMapping("/colecciones/{idColeccion}/eliminar")
public String eliminarColeccion(@PathVariable String idColeccion) {
  try {
    agregadorService.eliminarColeccion(idColeccion);
    return "redirect:/colecciones";
  } catch (Exception e) {
    return "redirect:/";
  }
}

@PostMapping("/colecciones/{idColeccion}/actualizar")
public String actualizarColecion(@PathVariable String idColeccion,
                                 @ModelAttribute("coleccion") ColeccionNuevaDto coleccion,
                                 BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
  try {
    System.out.println(coleccion);
    System.out.println(idColeccion);
    if(coleccion.getAlgoritmo().isBlank()) coleccion.setAlgoritmo(null);
    agregadorService.actualizarColeccion(idColeccion, coleccion);
    return "redirect:/colecciones";
  } catch(Exception e) {
    return "redirect:/";
  }
}

@GetMapping("/colecciones/{idColeccion}/editar")
public String mostrarEdicionColeccion(@PathVariable String idColeccion, Model model) {
  try {
    Coleccion coleccion = agregadorService.obtenerColeccionPorId(idColeccion);
    ColeccionNuevaDto coleccionNueva = new ColeccionNuevaDto();
    coleccionNueva.setTitulo(coleccion.getTitulo());
    coleccionNueva.setAlgoritmo(coleccion.getAlgoritmoConsenso());
    coleccionNueva.setDescripcion(coleccion.getDescripcion());
    if(coleccion.getFuentes() != null) {
      List<FuenteNuevaDto> fuentesColeccion = new ArrayList<>();
      coleccion.getFuentes().forEach(f -> {
        FuenteNuevaDto fuenteNuevaDto = new FuenteNuevaDto();
        fuenteNuevaDto.setTipoFuente(f.getTipoFuente());
        fuenteNuevaDto.setUrl(f.getUrl());
        fuentesColeccion.add(fuenteNuevaDto);
      });
      coleccionNueva.setFuentes(fuentesColeccion);
    }
    model.addAttribute("coleccionId", coleccion.getId());
    model.addAttribute("coleccion", coleccionNueva);
    return "/coleccion/editar";
  }
  catch (Exception ex) {
    //redirectAttributes.addFlashAttribute("mensaje", ex.getMessage());
    return "redirect:/colecciones";
  }
}
  @PostMapping("/colecciones/crear")
public String crearColeccion(@ModelAttribute("coleccion")ColeccionNuevaDto coleccionNueva,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
  try {
    agregadorService.crearColeccion(coleccionNueva);

//    redirectAttributes.addFlashAttribute("mensaje", "Alumno creado exitosamente");
//    redirectAttributes.addFlashAttribute("tipoMensaje", "success");
    return "redirect:/colecciones";
  }
  catch (Exception e) {
    return "redirect:/colecciones";
  }
}

  @GetMapping("/colecciones/nuevaColeccion")
  public String mostrarFormularioColeccion(Model model) {
    model.addAttribute("coleccion", new ColeccionNuevaDto());
    return "coleccion/crear";
  }


  @GetMapping("/colecciones/{idColeccion}/hechos")
  public String getHechosDeColeccion(@PathVariable String idColeccion, Model model) {
    //necesito recibir los hechos por parametro
    ColeccionDetallesDto coleccionDetalles = agregadorService.getHechosColeccion(idColeccion);
    List<HechoDto> hechos = coleccionDetalles.getData();
    model.addAttribute("paginaActual", coleccionDetalles.getCurrentPage());
    model.addAttribute("paginasTotales", coleccionDetalles.getTotalPages());
    model.addAttribute("hechos", hechos);
    model.addAttribute("idColeccion", idColeccion);
    model.addAttribute("titulo", "hechos de coleccion " + idColeccion);
    model.addAttribute("filtros", new FiltrosDto());
    return "coleccion/hechosColeccion";
  }

  @GetMapping("/colecciones")
  public String getColecciones(Model model) {
    List<Coleccion> colecciones = agregadorService.obtenerColecciones();
    model.addAttribute("colecciones",  colecciones);
    return "coleccion/colecciones";
  }

  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/hecho")
  public String detallesHechoSolicitudEliminacion (@PathVariable Long idSolicitud, Model model) {
    SolicitudEliminacionDetallesDto solicitudEliminacionDetallesDto = agregadorService.obtenerSolicitud(idSolicitud);
    HechoDetallesDto hecho = agregadorService.getDetallesHecho(solicitudEliminacionDetallesDto.getIdHecho());
    model.addAttribute("idSolicitud", solicitudEliminacionDetallesDto.getId());
    model.addAttribute("hecho", hecho);
    return "solicitudes/detallesHechoSolicitudEliminacion";
  }
  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/aceptar")
  public String procesarAceptacionSolicitud(@PathVariable Long idSolicitud) {
    agregadorService.aceptarSolicitud(idSolicitud);
    return "redirect:/panel-control/solicitudesEliminacion";
  }
  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/rechazar")
  public String procesarRechazoSolicitud(@PathVariable Long idSolicitud) {
    agregadorService.rechazarSolicitud(idSolicitud);
    return "redirect:/panel-control/solicitudesEliminacion";
  }
  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}")
  public String verDetallesSolicitud(Model model, @PathVariable Long idSolicitud) {
    SolicitudEliminacionDetallesDto solicitudEliminacionDetallesDto = agregadorService.obtenerSolicitud(idSolicitud);
    model.addAttribute("solicitud", solicitudEliminacionDetallesDto);
    model.addAttribute("pendiente", solicitudEliminacionDetallesDto.getEstadoActual() == "PENDIENTE");
    return "solicitudes/solicitudEliminacionDetalles";
  }
  @GetMapping("/panel-control/solicitudesEliminacion")
  public String mostrarSolicitudesEliminacion(Model model, @RequestParam(defaultValue = "1") int page,  @RequestParam(required = false, defaultValue = "true") Boolean pendientes) {
    SolicitudesPaginasDto solicitudesPaginadoDto = agregadorService.obtenerSolicitudes(page, pendientes);
    System.out.println(solicitudesPaginadoDto.getCurrentPage());
    System.out.println(solicitudesPaginadoDto.getTotalPages());
    model.addAttribute("page", solicitudesPaginadoDto.getCurrentPage());
    model.addAttribute("totalPages", solicitudesPaginadoDto.getTotalPages());
    model.addAttribute("solicitudes", solicitudesPaginadoDto.getData());
    model.addAttribute("pendientes", pendientes);
    return "solicitudes/solicitudesEliminacion";
  }
  @GetMapping("/panel-control")
  public String mostrarPanelControl(Model model) {
    ResumenActividadDto resumenActividadDto = agregadorService.obtenerResumenActividad();
    //aca irian las estadisticas
    model.addAttribute("hechosTotales", resumenActividadDto.getHechostotales());
    model.addAttribute("fuentesTotales", resumenActividadDto.getFuentesTotales());
    model.addAttribute("solicitudesEliminacion", resumenActividadDto.getSolicitudesEliminacion());
    return "panelControl";
  }
}