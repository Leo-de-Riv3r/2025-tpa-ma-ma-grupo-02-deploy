package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.Coleccion;
import ar.edu.utn.frba.dds.models.ColeccionDetallesDto;
import ar.edu.utn.frba.dds.models.ColeccionNuevaDto;
import ar.edu.utn.frba.dds.models.EstadisticaDto;
import ar.edu.utn.frba.dds.models.FiltrosDto;
import ar.edu.utn.frba.dds.models.FuenteNuevaDto;
import ar.edu.utn.frba.dds.models.HechoDetallesDto;
import ar.edu.utn.frba.dds.models.HechoDto;
import ar.edu.utn.frba.dds.models.HechoManualDTO;
import ar.edu.utn.frba.dds.models.NuevaEstadisticaDto;
import ar.edu.utn.frba.dds.models.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDetallesDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDto;
import ar.edu.utn.frba.dds.models.SolicitudesPaginasDto;
import ar.edu.utn.frba.dds.services.AgregadorService;
import ar.edu.utn.frba.dds.services.EstadisticaService;
import ar.edu.utn.frba.dds.services.FuenteDinamicaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
  private final AgregadorService agregadorService;
  private final EstadisticaService estadisticaService;
  private final FuenteDinamicaService fuenteDinamicaService;
  @Value("${agregador.service.url}")
  private String agregadorUrl;

  public MainController(AgregadorService agregadorService, EstadisticaService estadisticaService, FuenteDinamicaService fuenteDinamicaService) {
    this.agregadorService = agregadorService;
    this.estadisticaService = estadisticaService;
    this.fuenteDinamicaService = fuenteDinamicaService;
  }

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/crear-hecho")
  public String subirHecho(
      Model model){
    model.addAttribute("hechoDto", new HechoManualDTO());
    return "subirHechos/formularioHecho.html";
  }

  @GetMapping("/panel-control/hechosSubidos")
  public String mostrarHechosSubidos(Model model) {

    return "/hechosSubidos";
  }
  @PostMapping("/subir-hecho") // Asegúrate que esta URL coincida con el th:action
  public String procesarCreacionDeHecho(
      @ModelAttribute HechoManualDTO hechoDto,
      @RequestParam(value = "multimedia", required = false) List<MultipartFile> multimedia,
      HttpServletRequest request
      ) {
    //hechosService.crearHecho(hechoDto, multimedia);
    Object username = request.getSession().getAttribute("username");

    if (username != null) hechoDto.setAutor(username.toString());

    fuenteDinamicaService.crearHecho(hechoDto, multimedia);
    return "redirect:/colecciones";
  }

  @PostMapping("/solicitarEliminacion")
  public String procesarSolicitudEliminacion(@ModelAttribute("solicitudEliminacion") SolicitudEliminacionDto solicitud, HttpServletRequest request) {
    //envio solicitud
    Object username = request.getSession().getAttribute("username");

    if (username != null) solicitud.setCreador(username.toString());
    else solicitud.setCreador(" ");
    agregadorService.enviarSolicitud(solicitud);
    return "redirect:/colecciones";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos/{idHecho}/solicitudEliminacion")
  public String mostrarFormularioSolicitud(@PathVariable String idColeccion, @PathVariable Long idHecho, Model model) {
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

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/{idColeccion}/eliminar")
  public String eliminarColeccion(@PathVariable String idColeccion) {
      agregadorService.eliminarColeccion(idColeccion);
      return "redirect:/colecciones";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/{idColeccion}/actualizar")
  public String actualizarColecion(@PathVariable String idColeccion, @ModelAttribute("coleccion") ColeccionNuevaDto coleccion, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
      if (coleccion.getAlgoritmo().isBlank()) coleccion.setAlgoritmo(null);
      agregadorService.actualizarColeccion(idColeccion, coleccion);
      return "redirect:/colecciones";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/{idColeccion}/editar")
  public String mostrarEdicionColeccion(@PathVariable String idColeccion, Model model) {
      Coleccion coleccion = agregadorService.obtenerColeccionPorId(idColeccion);
      ColeccionNuevaDto coleccionNueva = new ColeccionNuevaDto();
      coleccionNueva.setTitulo(coleccion.getTitulo());
      coleccionNueva.setAlgoritmo(coleccion.getAlgoritmoConsenso());
      coleccionNueva.setDescripcion(coleccion.getDescripcion());
      if (coleccion.getFuentes() != null) {
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

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/crear")
  public String crearColeccion(@ModelAttribute("coleccion") ColeccionNuevaDto coleccionNueva, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
      agregadorService.crearColeccion(coleccionNueva);
      return "redirect:/colecciones";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/nuevaColeccion")
  public String mostrarFormularioColeccion(Model model) {
    model.addAttribute("coleccion", new ColeccionNuevaDto());
    return "coleccion/crear";
  }


  @GetMapping("/colecciones/{idColeccion}/hechos")
  public String getHechosDeColeccion(@PathVariable String idColeccion, Model model, @ModelAttribute("filtros") FiltrosDto filtros, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
      ColeccionDetallesDto coleccionDetalles = agregadorService.getHechosColeccion(idColeccion, filtros, page);
      List<HechoDto> hechos = coleccionDetalles.getData();
      model.addAttribute("paginaActual", coleccionDetalles.getCurrentPage());
      model.addAttribute("paginasTotales", coleccionDetalles.getTotalPages());
      model.addAttribute("hechos", hechos);
      model.addAttribute("idColeccion", idColeccion);
      model.addAttribute("titulo", "hechos de coleccion " + idColeccion);
      model.addAttribute("filtros", filtros);
      return "coleccion/hechosColeccion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/{idColeccion}/crearEstadistica")
  public String crearEstadisticaColeccion(@PathVariable String idColeccion, @ModelAttribute("nuevaEstadistica") NuevaEstadisticaDto nuevaEstadisticaDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    try {
      estadisticaService.crearEstadistica(nuevaEstadisticaDto);
      return "redirect:/colecciones";
    } catch (IllegalArgumentException e) {
      // Captura mensaje de error y redirige de nuevo al formulario
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/colecciones/{idColeccion}/nuevaEstadistica";
    }
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/{idColeccion}/nuevaEstadistica")
  public String mostrarFormulario(@PathVariable String idColeccion, Model model) {
    NuevaEstadisticaDto nuevaEstadisticaDto = new NuevaEstadisticaDto();
    nuevaEstadisticaDto.setUrlColeccion(agregadorUrl + "/colecciones/" + idColeccion);
    model.addAttribute("nuevaEstadistica", nuevaEstadisticaDto);
    if (!model.containsAttribute("error")) {
      model.addAttribute("error", null);
    }
    return "coleccion/nuevaEstadistica";
  }

  @GetMapping("/colecciones")
  public String getColecciones(Model model, RedirectAttributes redirectAttributes) {
      List<Coleccion> colecciones = agregadorService.obtenerColecciones();
      model.addAttribute("colecciones", colecciones);
    return "coleccion/colecciones";
  }

  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/hecho")
  public String detallesHechoSolicitudEliminacion(@PathVariable Long idSolicitud, Model model) {
    SolicitudEliminacionDetallesDto solicitudEliminacionDetallesDto = agregadorService.obtenerSolicitud(idSolicitud);
    HechoDetallesDto hecho = agregadorService.getDetallesHecho(solicitudEliminacionDetallesDto.getIdHecho());
    model.addAttribute("idSolicitud", solicitudEliminacionDetallesDto.getId());
    model.addAttribute("hecho", hecho);
    return "solicitudes/detallesHechoSolicitudEliminacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesEliminacion/{idSolicitud}/aceptar")
  public String procesarAceptacionSolicitud(@PathVariable Long idSolicitud) {
    agregadorService.aceptarSolicitud(idSolicitud);
    return "redirect:/panel-control/solicitudesEliminacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
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

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesEliminacion")
  public String mostrarSolicitudesEliminacion(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "true") Boolean pendientes) {
    SolicitudesPaginasDto solicitudesPaginadoDto = agregadorService.obtenerSolicitudes(page, pendientes);
    model.addAttribute("page", solicitudesPaginadoDto.getCurrentPage());
    model.addAttribute("totalPages", solicitudesPaginadoDto.getTotalPages());
    model.addAttribute("solicitudes", solicitudesPaginadoDto.getData());
    model.addAttribute("pendientes", pendientes);
    return "solicitudes/solicitudesEliminacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control")
  public String mostrarPanelControl(Model model) {
      ResumenActividadDto resumenActividadDto = agregadorService.obtenerResumenActividad();
      //aca irian las estadisticas
      model.addAttribute("hechosTotales", resumenActividadDto.getHechostotales());
      model.addAttribute("fuentesTotales", resumenActividadDto.getFuentesTotales());
      model.addAttribute("solicitudesEliminacion", resumenActividadDto.getSolicitudesEliminacion());

      List<EstadisticaDto> estadisticas = estadisticaService.obtenerEstadisticas();
      model.addAttribute("estadisticas", estadisticas);
    return "panelControl";
  }

  @GetMapping("/solicitudes-usuario")
  public String mostrarSolicitudesEliminacionCreadasPor(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "true") Boolean pendientes, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object username = request.getSession().getAttribute("username");
    if (username != null) {
        SolicitudesPaginasDto solicitudesPaginadoDto = agregadorService.obtenerSolicitudesCreadasPor(page, pendientes);
        model.addAttribute("page", solicitudesPaginadoDto.getCurrentPage());
        model.addAttribute("totalPages", solicitudesPaginadoDto.getTotalPages());
        model.addAttribute("solicitudes", solicitudesPaginadoDto.getData());
        model.addAttribute("pendientes", pendientes);
    } else {
      return "login";
    }
    return "solicitudes/solicitudesEliminacion";
  }

  @GetMapping("/home")
  public String getLandingPage() {
    return "home";
  }
}