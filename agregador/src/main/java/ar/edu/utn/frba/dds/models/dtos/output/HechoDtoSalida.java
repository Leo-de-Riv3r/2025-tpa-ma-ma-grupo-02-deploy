package ar.edu.utn.frba.dds.models.dtos.output;
import ar.edu.utn.frba.dds.models.entities.Multimedia;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class HechoDtoSalida {
  private Long id;
  private String titulo;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia="";
  private String municipio="";
  private String departamento="";
  private String tipoFuente;
  private String nombreAutor;
  private LocalDateTime fechaAcontecimiento;
}
