package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.TipoOrigen;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity @Table(name = "origen")
public class Origen {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_origen")
  private TipoOrigen tipo;
  @Column(name = "nombre_autor")
  private String nombreAutor;
  @Column(name = "apellido_autor")
  private String apellidoAutor;
  @Column(name = "edad_autor")
  private Integer edadAutor;

  public Origen(TipoOrigen tipo, String nombreAutor) {
    this.tipo = tipo;
    this.nombreAutor = nombreAutor;
  }
}
