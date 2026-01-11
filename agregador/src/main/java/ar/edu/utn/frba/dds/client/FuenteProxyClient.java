package ar.edu.utn.frba.dds.client;

import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "fuente-proxy")
public interface FuenteProxyClient {
  @GetMapping("/api/hechos")
  List<HechoDTOEntrada> getHechos();
}