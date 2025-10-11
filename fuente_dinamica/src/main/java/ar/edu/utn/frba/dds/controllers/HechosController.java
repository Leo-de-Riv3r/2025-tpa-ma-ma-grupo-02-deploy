package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.services.IHechosService;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/hechos")
public class HechosController {
  private final IHechosService hechosService;

  public HechosController(IHechosService hechosService) {
    this.hechosService = hechosService;
  }

  @GetMapping
  public List<HechoOutputDTO> getHechos() {
    return hechosService.getHechos();
  }

  @GetMapping("/{id}")
  public HechoOutputDTO getHechoById(@PathVariable Long id) {
    return hechosService.getHechoById(id); // TODO: try cath
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public HechoOutputDTO crearHecho(
          @RequestPart("hecho") HechoInputDTO hechoDto,
          @RequestPart(value = "multimedia", required = false) List<MultipartFile> multimedia) {
      return hechosService.crearHecho(hechoDto, multimedia);
  }
}
