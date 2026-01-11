package ar.edu.utn.frba.dds.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "agregador",
    path = "/fuentes"
)
public interface AgregadorClient {

  @PostMapping("/refrescar-dinamica")
  void refrescarDinamica();
}