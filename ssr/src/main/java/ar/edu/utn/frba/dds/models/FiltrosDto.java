package ar.edu.utn.frba.dds.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class FiltrosDto {
  String curados;
  String categoria;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  LocalDate fecha_acontecimiento_desde;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  LocalDate fecha_acontecimiento_hasta;
  String provincia;
  String municipio;
  String departamento;
}
