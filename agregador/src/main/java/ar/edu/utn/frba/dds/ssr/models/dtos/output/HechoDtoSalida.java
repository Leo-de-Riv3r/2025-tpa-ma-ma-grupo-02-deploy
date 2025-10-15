package ar.edu.utn.frba.dds.ssr.models.dtos.output;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class HechoDtoSalida {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private String provincia="";
  private String municipio="";
  private String departamento="";
  private Double latitud;
  private Double longitud;
  private String tipoFuente;
}
