package com.ddsi.utn.ba.ssr.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class FiltrosDto {
  String curados;
  String categoria;
  LocalDate fecha_acontecimiento_desde;
  LocalDate fecha_acontecimiento_hasta;
  String provincia;
  String municipio;
  String departamento;
}
