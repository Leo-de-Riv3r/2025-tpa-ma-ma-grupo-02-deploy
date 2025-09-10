package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.DTO.input.FuenteCsvDTOInput;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fuentes")
public class FuentesController {
  private IFuenteEstaticaService fuenteEstaticaService;
  public FuentesController(IFuenteEstaticaService fuenteEstaticaService) {
    this.fuenteEstaticaService = fuenteEstaticaService;
  }

  @GetMapping("/{id}/hechos")
  public List<Hecho> getHechos(
      @PathVariable(required = true) Long id
      ,@RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "per_page", required = false, defaultValue = "100") int perPage) {
    return fuenteEstaticaService.getHechos(id, page, perPage);
  }

  @PostMapping("")
  public FuenteCsvDTOOutput crearFuenteCsv (@RequestBody FuenteCsvDTOInput dtofuente){
    return fuenteEstaticaService.crearNuevaFuente(dtofuente.getLink(), dtofuente.getSeparador());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> eliminarFuente(@PathVariable Long id){
    fuenteEstaticaService.eliminarFuente(id);
    return ResponseEntity.ok("Operacion completada");
  }

  @GetMapping("")
  public List<FuenteCsvDTOOutput> obtenerFuentes() {
    return fuenteEstaticaService.obtenerFuentesDTO();
  }
}
