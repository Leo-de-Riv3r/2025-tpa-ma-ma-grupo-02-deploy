package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.externalApi.GeoRefApiAdapter;
import ar.edu.utn.frba.dds.externalApi.NormalizadorUbicacionAdapter;
import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoAlgoritmo;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.ConsensoStrategy.IConsensoStrategy;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fuente")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@NoArgsConstructor
public abstract class Fuente {
  @Getter
  @Id
  protected String id  =UUID.randomUUID().toString();
  @Getter
  @Setter
  @Column
  protected String url;
  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  @Column
  protected TipoFuente tipoFuente;

  @OneToMany(cascade = CascadeType.ALL) @JoinColumn(name = "fuente_id" , referencedColumnName = "id")
  //@Transient
  protected Set<Hecho> hechos = new HashSet<>();

  @Column
  @Getter
  private Integer inactivo;
  @Transient
  protected NormalizadorUbicacionAdapter normalizadorLugar = new GeoRefApiAdapter();

  public Fuente(String url, TipoFuente tipoFuente) {
    this.url = url;
    this.tipoFuente = tipoFuente;
  }

  public abstract void refrescarHechos();

  public abstract Set<Hecho> getHechos();

  public void setHechos(List<Hecho> hechos) {
    this.hechos.clear();
    this.hechos.addAll(hechos);
  }

  public Boolean existeHecho(Hecho hecho) {
    return this.hechos.stream()
        .anyMatch(h -> Objects.equals(h.getTitulo(), hecho.getTitulo()) && Objects.equals(h.getCategoria(), hecho.getCategoria()) && Objects.equals(h.getDescripcion(), hecho.getDescripcion()));
  }
}
