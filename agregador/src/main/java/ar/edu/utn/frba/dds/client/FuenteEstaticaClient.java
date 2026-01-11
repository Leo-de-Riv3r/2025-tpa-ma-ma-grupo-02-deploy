package ar.edu.utn.frba.dds.client;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fuente-estatica")
public interface FuenteEstaticaClient {
  @GetMapping(value = "/api/fuentes/{id}/hechos")
  List<HechoDTOEntrada> getHechos(@PathVariable Long id);
}
