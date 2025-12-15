package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.exceptions.TokenExpiradoException;
import ar.edu.utn.frba.dds.models.BlockIpDto;
import ar.edu.utn.frba.dds.models.BlockedIpResDto;
import ar.edu.utn.frba.dds.models.Coleccion;
import ar.edu.utn.frba.dds.models.ColeccionDetallesDto;
import ar.edu.utn.frba.dds.models.ColeccionHechosDto;
import ar.edu.utn.frba.dds.models.ColeccionNuevaDto;
import ar.edu.utn.frba.dds.models.EstadisticaDto;
import ar.edu.utn.frba.dds.models.FiltrosDto;
import ar.edu.utn.frba.dds.models.FuenteNuevaDto;
import ar.edu.utn.frba.dds.models.HechoDetallesDto;
import ar.edu.utn.frba.dds.models.HechoDto;
import ar.edu.utn.frba.dds.models.HechoManualDTO;
import ar.edu.utn.frba.dds.models.HechoPaginacionDto;
import ar.edu.utn.frba.dds.models.HechoUpdateDTO;
import ar.edu.utn.frba.dds.models.MultimediaDto;
import ar.edu.utn.frba.dds.models.NuevaEstadisticaDto;
import ar.edu.utn.frba.dds.models.PaginacionDtoBlockedIp;
import ar.edu.utn.frba.dds.models.PaginacionDtoHechoDtoSalida;
import ar.edu.utn.frba.dds.models.ResumenActividadDto;
import ar.edu.utn.frba.dds.models.RevisionHechoDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDetallesDto;
import ar.edu.utn.frba.dds.models.SolicitudEliminacionDto;
import ar.edu.utn.frba.dds.models.SolicitudHechoDto;
import ar.edu.utn.frba.dds.models.SolicitudHechoInputDto;
import ar.edu.utn.frba.dds.models.SolicitudModificacionDto;
import ar.edu.utn.frba.dds.models.SolicitudesModificacionPaginado;
import ar.edu.utn.frba.dds.models.SolicitudesPaginasDto;
import ar.edu.utn.frba.dds.services.AgregadorService;
import ar.edu.utn.frba.dds.services.BlockedIpService;
import ar.edu.utn.frba.dds.services.EstadisticaService;
import ar.edu.utn.frba.dds.services.FuenteDinamicaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
  private final AgregadorService agregadorService;
  private final EstadisticaService estadisticaService;
  private final FuenteDinamicaService fuenteDinamicaService;
  private final BlockedIpService blockedIpService;
  @Value("${agregador.service.url}")
  private String agregadorUrl;

  @Value("${fuenteDinamica.service.url}")
  private String fuenteDinamicaUrl;

  @Value("${auth.service.url}")
  private String authServiceUrl;

  public MainController(AgregadorService agregadorService, EstadisticaService estadisticaService, FuenteDinamicaService fuenteDinamicaService, BlockedIpService blockedIpService) {
    this.agregadorService = agregadorService;
    this.estadisticaService = estadisticaService;
    this.fuenteDinamicaService = fuenteDinamicaService;
    this.blockedIpService = blockedIpService;
  }

  @GetMapping({"/", "home"})
  public String home(Model model) {
    try {
      List<Coleccion> colecciones = agregadorService.obtenerColecciones();

      if (colecciones != null && !colecciones.isEmpty()) {
        String idColeccion = colecciones.get(0).getId();
        ColeccionHechosDto resultado = agregadorService.getHechosColeccion(
            idColeccion,
            new FiltrosDto(),
            1);

        if (resultado != null && resultado.getHechos() != null) {
          List<HechoPaginacionDto> listaHechos = resultado.getHechos().getData();
          int limite = Math.min(listaHechos.size(), 6);

          model.addAttribute("hechos", listaHechos.subList(0, limite));
          model.addAttribute("idColeccion", idColeccion);
        }
      }
    } catch (Exception e) {
      System.err.println("Warning: " + e.getMessage());
    }
    return "home";
  }

  @PostMapping("/panel-control/controlIp/procesarDesbloqueoIp")
  public String procesarDesbloqueoIp(@ModelAttribute BlockIpDto blockIp, RedirectAttributes redirectAttributes) {
    blockedIpService.unblockIp(blockIp);
    return "redirect:/panel-control/controlIp";
  }

  @PostMapping("/panel-control/controlIp/procesarBloqueoIp")
  public String procesarBloqueoIp(@ModelAttribute BlockIpDto blockIp, RedirectAttributes redirectAttributes) {
    blockedIpService.blockIp(blockIp);
    return "redirect:/panel-control/controlIp";
  }

  @GetMapping("/panel-control/controlIp/{ip}/desbloquear")
  public String mostrarFormularioDesbloqueoIp(Model model, @PathVariable String ip) {
    BlockIpDto blockIpDto = new BlockIpDto();
    blockIpDto.setIp(ip);
    model.addAttribute("blockIp", blockIpDto);
    return "ipManager/desbloquearIp";
  }

  @GetMapping("/panel-control/controlIp/{ip}/bloquear")
  public String mostrarFormularioBloqueoIp(Model model, @PathVariable String ip) {
    BlockIpDto blockIpDto = new BlockIpDto();
    blockIpDto.setIp(ip);
    model.addAttribute("blockIp", blockIpDto);
    return "ipManager/bloquearIp";
  }

  @PostMapping("/panel-control/controlIp/procesarSubmitIp")
  public String procesarSubmitIp(@ModelAttribute BlockIpDto blockIp, RedirectAttributes redirectAttributes) {
    try {
      blockedIpService.blockIp(blockIp);
      return "redirect:/panel-control/controlIp";
    } catch (Exception e) {
      if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.CONFLICT){
        return "redirect:/panel-control/controlIp/agregar?conflict";
      }
    }
    return "redirect:/panel-control/controlIp";
  }
  @GetMapping("/panel-control/controlIp/agregar")
  public String formularioAgregarIp(Model model) {
    model.addAttribute("blockIp", new BlockIpDto());
    return "ipManager/agregarIp";
  }

//  @GetMapping("/panel-control/controlIp/")

  @GetMapping("/panel-control/controlIp")
  public String mostrarListaIps(Model model, @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(name = "perPage", required = false, defaultValue = "10") int perPage
                                ) {
    PaginacionDtoBlockedIp paginacionDtoBlockedIp = blockedIpService.getList(page, perPage);
    model.addAttribute("lista", paginacionDtoBlockedIp.getData());
    model.addAttribute("page", paginacionDtoBlockedIp.getCurrentPage());
    model.addAttribute("totalPages", paginacionDtoBlockedIp.getTotalPages());
    return "ipManager/ipList";
  }

  @GetMapping("/editar-hecho/{id}")
  public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    HechoUpdateDTO hechoExistente = fuenteDinamicaService.obtenerHechoEdicion(id);
    HechoManualDTO hechoForm = new HechoManualDTO();
    hechoForm.setTitulo(hechoExistente.getTitulo());
    hechoForm.setDescripcion(hechoExistente.getDescripcion());
    hechoForm.setCategoria(hechoExistente.getCategoria());
    hechoForm.setLatitud(hechoExistente.getLatitud());
    hechoForm.setLongitud(hechoExistente.getLongitud());
    hechoForm.setFechaAcontecimiento(hechoExistente.getFechaHecho());
    model.addAttribute("hechoDto", hechoForm);
    model.addAttribute("esEdicion", true);
    model.addAttribute("idHecho", id);
    model.addAttribute("tituloPagina", "Editar Hecho Existente");
    return "subirHechos/formularioHecho";
  }

  @PostMapping("/editar-hecho/{id}")
  public String procesarEdicionDeHecho(
      @PathVariable Long id,
      @ModelAttribute("hecho") HechoUpdateDTO hechoDto,
      @RequestParam(value = "multimedia", required = false) List<MultipartFile> multimediaFiles,
      RedirectAttributes redirectAttributes
  ) {
    try {
      fuenteDinamicaService.editarHecho(id, hechoDto, multimediaFiles);
      return "redirect:/hechos-usuario";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error al intentar editar el hecho: " + e.getMessage());
      return "redirect:/editar-hecho/" + id;
    }
  }

  @GetMapping("/hechos-usuario")
  public String visualizarHechosCreadorPor(Model model) {
    List<SolicitudHechoDto> solicitudHechoDtos = fuenteDinamicaService.obtenerHechosPorCreador();
    model.addAttribute("solicitudesHechos", solicitudHechoDtos);
    return "subirHechos/hechosUsuario";
  }

  @GetMapping("/crear-hecho")
  public String mostrarFormularioCrear(Model model) {
    model.addAttribute("hechoDto", new HechoManualDTO());
    model.addAttribute("esEdicion", false);
    model.addAttribute("tituloPagina", "Reportar un Nuevo Hecho");
    return "subirHechos/formularioHecho";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos")
  public String mostrarRevisionHechos(Model model) {
    List<SolicitudHechoDto> solicitudesHecho = fuenteDinamicaService.obtenerSolicitudesHecho();
    model.addAttribute("solicitudesHechos", solicitudesHecho);
    return "revisionHechos";
  }

  @GetMapping("/panel-control/revisionHechos/{idHecho}")
  public String mostrarDetallesSolicitudHecho(@PathVariable Long idHecho, Model model) {
    SolicitudHechoInputDto solicitud = fuenteDinamicaService.obtenerSolicitudById(idHecho);
    HechoDetallesDto hechoDto = new HechoDetallesDto();
    hechoDto.setId(idHecho);
    hechoDto.setTitulo(solicitud.getTitulo());
    hechoDto.setDescripcion(solicitud.getDescripcion());
    hechoDto.setCategoria(solicitud.getCategoria());
    hechoDto.setLatitud(solicitud.getLatitud());
    hechoDto.setLongitud(solicitud.getLongitud());
    hechoDto.setNombreAutor(solicitud.getAutor());

    if (solicitud.getFechaHecho() != null) {
      hechoDto.setFechaAcontecimiento(solicitud.getFechaHecho().toString());
    }

    if (solicitud.getMultimedia() != null) {
      List<MultimediaDto> mediaList = new ArrayList<>();
      solicitud.getMultimedia().forEach(m -> {
        MultimediaDto md = new MultimediaDto();
        md.setNombre(m.getNombre());
        md.setRuta(m.getRuta());
        md.setFormato(m.getFormato());
        mediaList.add(md);
      });
      hechoDto.setMultimedia(mediaList);
    }
    model.addAttribute("hecho", hechoDto);
    model.addAttribute("idColeccion", "revision-carga"); // Dummy ID
    model.addAttribute("modoValidacion", true);
    model.addAttribute("hechoId", idHecho); // Necesario para los links de los botones
    return "coleccion/detallesHecho";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos/{idHecho}/aceptar")
  public String aceptarSolicitudHecho(@PathVariable Long idHecho, Model model, HttpServletRequest request) {
    RevisionHechoDto revisionHechoDto = new RevisionHechoDto();
    revisionHechoDto.setSupervisor(request.getSession().getAttribute("username").toString());
    revisionHechoDto.setComentario("");

    fuenteDinamicaService.aceptarSolicitud(idHecho, revisionHechoDto);
    return "redirect:/panel-control/revisionHechos";
  }


  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/panel-control/revisionHechos/{idHecho}/aceptarConSugerencia")
  public String aceptarSolicitudHecho(@PathVariable Long idHecho, @ModelAttribute RevisionHechoDto revisionHechoDto) {
    fuenteDinamicaService.aceptarConSugerencias(idHecho, revisionHechoDto);
    return "redirect:/panel-control/revisionHechos";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos/{idHecho}/rechazoConSugerencias")
  public String mostrarFormularioSugerenciasRechazo(@PathVariable Long idHecho, Model model, HttpServletRequest request) {
    RevisionHechoDto revisionHechoDto = new RevisionHechoDto();
    revisionHechoDto.setSupervisor(request.getSession().getAttribute("username").toString());
    model.addAttribute("hechoId", idHecho);
    model.addAttribute("revisionHechoDto", revisionHechoDto);
    model.addAttribute("accionHecho", "rechazar");
    return "subirComentariosSolicitud";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/revisionHechos/{idHecho}/aceptarConSugerencias")
  public String mostrarFormularioSugerenciasAceptacion(@PathVariable Long idHecho, Model model, HttpServletRequest request) {
    RevisionHechoDto revisionHechoDto = new RevisionHechoDto();
    revisionHechoDto.setSupervisor(request.getSession().getAttribute("username").toString());
    model.addAttribute("hechoId", idHecho);
    model.addAttribute("revisionHechoDto", revisionHechoDto);
    model.addAttribute("accionHecho", "aceptarConSugerencia");
    return "subirComentariosSolicitud";
  }

  @PostMapping("/panel-control/revisionHechos/{idHecho}/rechazar")
  public String rechazarSolicitudHecho(@PathVariable Long idHecho, @ModelAttribute RevisionHechoDto revisionHechoDto) {
    fuenteDinamicaService.rechazarSolicitud(idHecho, revisionHechoDto);
    return "redirect:/panel-control/revisionHechos";
  }

  @PostMapping("/subir-hecho") // Asegúrate que esta URL coincida con el th:action
  public String procesarCreacionDeHecho(
      @ModelAttribute HechoManualDTO hechoDto,
      @RequestParam(value = "multimedia", required = false) List<MultipartFile> multimedia,
      HttpServletRequest request
      ) {

    Object username = request.getSession().getAttribute("username");

    if (username != null) hechoDto.setAutor(username.toString());

    fuenteDinamicaService.crearHecho(hechoDto, multimedia);
    return "redirect:/";
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
    model.addAttribute("idColeccion", idColeccion);
    return "coleccion/solicitudEliminacion";
  }

  //authorize with role ADMINISTRADOR or CONTRIBUYENTE

  @PostMapping("/procesarModificacionHecho")
  public String procesarModificacionHecho(@ModelAttribute("solicitud") SolicitudModificacionDto solicitud, BindingResult bindingResult, Model model, HttpServletRequest request) {
    solicitud.setCreador(request.getSession().getAttribute("username").toString());
    agregadorService.modificarHecho(solicitud);
    return "redirect:/colecciones";
  }

  @GetMapping("/colecciones/{idColeccion}/hechos/{idHecho}/solicitudModificacion")
  public String mostrarFormularioModificacion(@PathVariable String idColeccion, @PathVariable Long idHecho, Model model, HttpServletRequest request) {
    HechoDetallesDto hechoDetallesDto = agregadorService.getDetallesHecho(idHecho);
    SolicitudModificacionDto solicitud = new SolicitudModificacionDto();
    solicitud.setIdHecho(idHecho);
    solicitud.setCategoria(hechoDetallesDto.getCategoria());
    solicitud.setDescripcion(hechoDetallesDto.getDescripcion());
    solicitud.setFechaAcontecimiento(hechoDetallesDto.getFechaAcontecimiento());
    solicitud.setLatitud(hechoDetallesDto.getLatitud());
    solicitud.setLongitud(hechoDetallesDto.getLongitud());
    solicitud.setTitulo(hechoDetallesDto.getTitulo());

    model.addAttribute("solicitud", solicitud);
    model.addAttribute("hechoId", idHecho);
    model.addAttribute("idColeccion", idColeccion);
    return "subirHechos/solicitudModificacion";
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
  @GetMapping("/colecciones/{idColeccion}/editar")
  public String mostrarFormularioEdicion(@PathVariable String idColeccion, Model model) {
    Coleccion coleccionExistente = agregadorService.obtenerColeccionPorId(idColeccion);
    ColeccionNuevaDto form = mapearAFormulario(coleccionExistente);

    model.addAttribute("coleccionForm", form);
    model.addAttribute("esEdicion", true);
    model.addAttribute("accion", "/colecciones/" + idColeccion + "/actualizar");
    return "coleccion/formColeccion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/{idColeccion}/actualizar")
  public String actualizarColeccion(@PathVariable String idColeccion,
                                    @ModelAttribute("coleccionForm") ColeccionNuevaDto coleccion,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

    if (coleccion.getAlgoritmoConsenso() != null && coleccion.getAlgoritmoConsenso().isBlank()) {
      coleccion.setAlgoritmoConsenso(null);
    }

    try {
      agregadorService.actualizarColeccion(idColeccion, coleccion);
      redirectAttributes.addFlashAttribute("success", "Colección actualizada correctamente.");
    } catch (TokenExpiradoException e) {
      throw e;
    }
    catch (Exception e) {
      if (e instanceof TokenExpiradoException) {
        throw e;
      }
      redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
      return "redirect:/colecciones/" + idColeccion + "/editar";
    }

    return "redirect:/colecciones";
  }


  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @PostMapping("/colecciones/crear")
  public String crearColeccion(@ModelAttribute("coleccion") ColeccionNuevaDto coleccionNueva, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    try {
      agregadorService.crearColeccion(coleccionNueva);
      redirectAttributes.addFlashAttribute("success", "Colección creada correctamente.");
      return "redirect:/colecciones";
    }
    catch (TokenExpiradoException e) {
      throw e;
    }
     catch (Exception e) {
       redirectAttributes.addFlashAttribute("error", "Error al crear: " + e.getMessage());
       return "redirect:/colecciones/nuevaColeccion";
     }
  }





  @GetMapping("/colecciones/{idColeccion}/hechos")
  public String getHechosDeColeccion(@PathVariable String idColeccion, Model model, @ModelAttribute("filtros") FiltrosDto filtros, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
      ColeccionHechosDto coleccionHechosDto = agregadorService.getHechosColeccion(idColeccion, filtros, page);
      List<HechoPaginacionDto> hechos = coleccionHechosDto.getHechos().getData();
      Coleccion coleccion = agregadorService.obtenerColeccionPorId(idColeccion);
      String tituloAMostrar = (coleccion != null) ? coleccion.getTitulo() : "Colección no encontrada";
      model.addAttribute("paginaActual", coleccionHechosDto.getHechos().getCurrentPage());
      model.addAttribute("paginasTotales", coleccionHechosDto.getHechos().getTotalPages());
      model.addAttribute("hechos", hechos);
      model.addAttribute("idColeccion", idColeccion);
      model.addAttribute("titulo", tituloAMostrar);
      model.addAttribute("filtros", filtros);
      model.addAttribute("listaProvincias", agregadorService.obtenerProvincias());
      model.addAttribute("listaMunicipios", agregadorService.obtenerMunicipios());
      model.addAttribute("listaDepartamentos", agregadorService.obtenerDepartamentos());
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
    model.addAttribute("esRevision", true);
    model.addAttribute("idColeccion", "admin-view");
    return "coleccion/detallesHecho";
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
  @GetMapping("/panel-control/solicitudesModificacion/{idSolicitud}/aceptar")
  public String procesarAceptacionSolicitudModificacion(@PathVariable Long idSolicitud) {
    agregadorService.aceptarSolicitudModificacion(idSolicitud);
    return "redirect:/panel-control/solicitudesModificacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesModificacion")
  public String verSolicitudes(Model model, @RequestParam(defaultValue = "1") int page) {
    SolicitudesModificacionPaginado solicitudesPaginadoDto = agregadorService.obtenerSolicitudesModificacion(page);
    model.addAttribute("page", solicitudesPaginadoDto.getCurrentPage());
    model.addAttribute("totalPages", solicitudesPaginadoDto.getTotalPages());
    model.addAttribute("solicitudes", solicitudesPaginadoDto.getData());
    return "solicitudesModificacionHecho";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/panel-control/solicitudesEliminacion")
  public String mostrarSolicitudesEliminacion(Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "true") Boolean pendientes) {
    SolicitudesPaginasDto solicitudesPaginadoDto = agregadorService.obtenerSolicitudes(page, pendientes);
    model.addAttribute("subtitulo", "Gestiona los reportes de hechos realizados por usuarios.");
    model.addAttribute("noSolicitudesMensaje", "Buen trabajo! Todo está al día.");
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
        model.addAttribute("subtitulo", "Gestiona los reportes de hechos que has realizado.");
        model.addAttribute("noSolicitudesMensaje", "No has realizado ninguna solicitud de eliminación aún.");
        model.addAttribute("page", solicitudesPaginadoDto.getCurrentPage());
        model.addAttribute("totalPages", solicitudesPaginadoDto.getTotalPages());
        model.addAttribute("solicitudes", solicitudesPaginadoDto.getData());
        model.addAttribute("pendientes", pendientes);
    } else {
      return "login";
    }
    return "solicitudes/solicitudesEliminacion";
  }

  @PreAuthorize("hasRole('ADMINISTRADOR')")
  @GetMapping("/colecciones/nuevaColeccion")
  public String mostrarFormularioCreacion(Model model) {
    model.addAttribute("coleccionForm", new ColeccionNuevaDto());
    model.addAttribute("esEdicion", false);
    model.addAttribute("accion", "/colecciones/crear");
    return "coleccion/formColeccion";
  }

  private ColeccionNuevaDto mapearAFormulario(Coleccion coleccion) {
    ColeccionNuevaDto form = new ColeccionNuevaDto();

    form.setTitulo(coleccion.getTitulo());
    form.setDescripcion(coleccion.getDescripcion());
    form.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso());
    if (coleccion.getFuentes() != null) {
      List<FuenteNuevaDto> fuentesDto = new ArrayList<>();
      coleccion.getFuentes().forEach(f -> {
        FuenteNuevaDto fd = new FuenteNuevaDto();
        fd.setTipoFuente(f.getTipoFuente());
        fd.setUrl(f.getUrl());
        fuentesDto.add(fd);
      });
      form.setFuentes(fuentesDto);
    }

    if (coleccion.getCriterios() != null) {
      form.setCriterios(coleccion.getCriterios());
    } else {
      form.setCriterios(new ArrayList<>());
    }

    return form;
  }

}