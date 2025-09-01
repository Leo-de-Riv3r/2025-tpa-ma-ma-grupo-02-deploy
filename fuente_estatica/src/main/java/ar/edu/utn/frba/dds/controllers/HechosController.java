package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.DTO.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import ar.edu.utn.frba.dds.models.DTO.HechosPagDTO;
import ar.edu.utn.frba.dds.models.DTO.fuenteCsvDTO;
import ar.edu.utn.frba.dds.models.utils.ResponseEntityFactory;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hechos")
public class HechosController {
  private IFuenteEstaticaService fuenteEstaticaService;
  public HechosController(IFuenteEstaticaService fuenteEstaticaService) {
    this.fuenteEstaticaService = fuenteEstaticaService;
  }

  @GetMapping
  public List<HechoDTO> getHechos(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "per_page", required = false, defaultValue = "10") int perPage) {
    return fuenteEstaticaService.getHechos(page, perPage);
  }

  @PostMapping("/fuenteCsv")
  public ResponseEntity<Map<String, Object>> setFuenteCsv(@RequestBody fuenteCsvDTO fuenteCsv) {
    fuenteEstaticaService.extraeryGuardarHechos(fuenteCsv.getLink(), fuenteCsv.getSeparador());
    ResponseEntity<Map<String, Object>> response = ResponseEntityFactory.createOkEntity("Se ha actualizado la fuente exitosamente");
    return response;
  }
}
