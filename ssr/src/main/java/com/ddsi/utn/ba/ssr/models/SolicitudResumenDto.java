package com.ddsi.utn.ba.ssr.models;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudResumenDto {
  private Long id;
  private String titulo;
  private LocalDateTime fecha;
  private String estadoActual;
  private Integer esSpam;
}
