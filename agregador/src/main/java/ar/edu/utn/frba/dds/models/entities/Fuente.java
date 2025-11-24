package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.utils.HechoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.web.reactive.function.client.WebClient;

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

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "fuente_hecho",
      joinColumns = @JoinColumn(name = "fuente_id" , referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  protected Set<Hecho> hechos = new HashSet<>();

  public Fuente(String url, TipoFuente tipoFuente) {
    this.url = url;
    this.tipoFuente = tipoFuente;
  }

  public abstract Set<Hecho> obtenerHechosRefrescados(HechoConverter hechoConverter, WebClient webClient);

  public abstract Set<Hecho> getHechos();

  public Boolean existeHecho(Hecho hecho) {
    return this.hechos.stream()
        .anyMatch(h -> h.getTitulo() == hecho.getTitulo() && h.getCategoria() ==  hecho.getCategoria() && h.getDescripcion() == hecho.getDescripcion());
  }

  public void addHechos(Set<Hecho> hechos) {
    this.hechos.addAll(hechos);
  }

  public void addHecho(Hecho hecho) {this.hechos.add(hecho);}
}
