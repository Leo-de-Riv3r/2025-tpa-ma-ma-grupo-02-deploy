package ar.edu.utn.frba.dds.client;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "fuente-dinamica")
public interface FuenteDinamicaClient {
  @GetMapping("/hechos")
  List<HechoDTOEntrada> getHechos();
}
