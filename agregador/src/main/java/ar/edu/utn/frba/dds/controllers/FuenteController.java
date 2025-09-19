package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.dtos.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.dtos.FuenteNuevoDTO;
import ar.edu.utn.frba.dds.services.FuenteService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FuenteController {
  private final FuenteService fuenteService;

  public FuenteController (FuenteService fuenteService){
    this.fuenteService = fuenteService;
  }

  @GetMapping("/fuentes")
  public List<FuenteDTOOutput> getFuentes() {
    return fuenteService.getFuentes();
  }

  @PostMapping("/fuentes")
  public FuenteDTOOutput createFuente(@RequestBody FuenteNuevoDTO fuenteNuevoDTO) {
    return fuenteService.createFuente(fuenteNuevoDTO);
  }

  @PutMapping("/fuentes")
  public void actualizarFuentes(){
    fuenteService.actualizarFuentes();
  }

  @DeleteMapping("/fuentes/{idFuente}")
  public ResponseEntity<String> deleteFuente(@PathVariable String idFuente) {
    return fuenteService.eliminarFuente(idFuente);
  }
}
