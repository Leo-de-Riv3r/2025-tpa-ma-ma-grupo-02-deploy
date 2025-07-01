package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.HechoDTOEntrada;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

public abstract class Fuente {
  @Getter
  protected final String id;
  @Getter
  protected final String url;
  @Getter
  protected final TipoFuente tipoFuente;
  protected Set<Hecho> hechos = Set.of();

  protected Fuente(String url, TipoFuente tipoFuente) {
    this.id = UUID.randomUUID().toString();
    this.url = url;
    this.tipoFuente = tipoFuente;
  }

  public abstract void refrescarHechos();

  public abstract Set<Hecho> getHechos(Set<IFiltroStrategy> filtros);

  public boolean cumpleFiltros(Set<IFiltroStrategy> filtros, Hecho hecho) {
    return filtros == null || filtros.isEmpty() || filtros.stream().allMatch(f -> f.cumpleFiltro(hecho));
  }

  public static Fuente convertirFuenteDTOAFuente(FuenteDTO dto) {
    TipoFuente tipoFuente = TipoFuente.valueOf(dto.getTipoFuente());
    if (dto.getUrl() == null || dto.getUrl().isEmpty()) {
      throw new IllegalArgumentException("La URL de la fuente no puede ser nula o vacía");
    }
    return tipoFuente == TipoFuente.PROXY_METAMAPA
        ? new FuenteProxyMetamapa(dto.getUrl())
        : new FuenteDefault(dto.getUrl(), tipoFuente);
  }
}
