package ar.edu.utn.frba.dds.models.entities;


import ar.edu.utn.frba.dds.externalApi.GeoRefApiAdapter;
import ar.edu.utn.frba.dds.externalApi.NormalizadorUbicacionAdapter;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.UUID;
import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity @Table(name = "hecho")
public class Hecho {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String titulo;
  @Column(length = 2000)
  private String descripcion;
  @Column
  private String categoria;
  //@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  //lo hago asi para lidiar con los hechos de fuente proxy(no se persisten)
  @Embedded
  private Ubicacion ubicacion;
  @Column
  private LocalDateTime fechaAcontecimiento;
  @Column
  private LocalDateTime fechaCarga;
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name="origen_id", referencedColumnName = "id")
  private Origen origen;

  @OneToMany(mappedBy = "hecho", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Multimedia> multimedia = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Hecho hecho)) {
      return false;
    }
    return titulo.equals(hecho.titulo);
  }

//  private static EntityManagerFactory emf =
//      Persistence.createEntityManagerFactory("AMD");
//  private static EntityManager em = emf.createEntityManager();
  public static Hecho convertirHechoDTOAHecho(HechoDTOEntrada dto, TipoFuente tipoFuente) {
    Ubicacion ubicacion = new Ubicacion();
    ubicacion.setLatitud(dto.getLatitud());
    ubicacion.setLongitud(dto.getLongitud());
    Origen origenExistente = new Origen();
    origenExistente.setTipo(tipoFuente);
    Hecho hecho = new Hecho();
    hecho.setTitulo(dto.getTitulo());
    hecho.setDescripcion(dto.getDescripcion());
    hecho.setCategoria(dto.getCategoria());
    hecho.setUbicacion(ubicacion);
    hecho.setFechaAcontecimiento(dto.getFechaHecho());
    hecho.setFechaCarga(dto.getCreatedAt());
    hecho.setOrigen(origenExistente);

    return hecho;
  }

  public boolean cumpleFiltros(Set<IFiltroStrategy> filtros) {
    return filtros == null || filtros.isEmpty() || filtros.stream().allMatch(f -> f.cumpleFiltro(this));
  }
}
