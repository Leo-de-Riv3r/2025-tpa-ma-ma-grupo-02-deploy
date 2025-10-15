package ar.edu.utn.frba.dds.ssr.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Setter;

@Data
public class MultimediaDto {
  private String nombre;
  private String ruta;
  private String formato;
}
