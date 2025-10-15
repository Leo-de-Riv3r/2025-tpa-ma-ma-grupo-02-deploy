package ar.edu.utn.frba.dds.ssr.models.entities;

import ar.edu.utn.frba.dds.ssr.models.enums.TipoOrigen;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "origen")
@AllArgsConstructor
@NoArgsConstructor
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
