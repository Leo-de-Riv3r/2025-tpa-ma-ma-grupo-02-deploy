package ar.edu.utn.frba.dds.ssr.models.entities.utils;

import ar.edu.utn.frba.dds.ssr.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.ssr.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.ssr.models.entities.Fuente;
import ar.edu.utn.frba.dds.ssr.models.entities.FuenteDefault;
import ar.edu.utn.frba.dds.ssr.models.entities.FuenteProxyMetamapa;
import ar.edu.utn.frba.dds.ssr.models.entities.enums.TipoFuente;
import org.springframework.stereotype.Component;

@Component
public class FuenteConverter {
  private final HechoConverter hechoConverter;

  public FuenteConverter(HechoConverter hechoConverter) {
    this.hechoConverter = hechoConverter;
  }

  public Fuente fromDto(FuenteDTO fuenteDTO) {
    if (fuenteDTO.getTipoFuente() == null || fuenteDTO.getUrl() == null){
      throw new IllegalArgumentException("La url y/o el tipo de fuente estan vacios");
    }
        TipoFuente tipoFuente = TipoFuente.valueOf(fuenteDTO.getTipoFuente().toUpperCase());
        Fuente fuente;
        if (tipoFuente == TipoFuente.PROXY_METAMAPA) {
          fuente = new FuenteProxyMetamapa(fuenteDTO.getUrl());
        } else if (tipoFuente == TipoFuente.DINAMICA || tipoFuente == TipoFuente.ESTATICA) {
          fuente = new FuenteDefault(fuenteDTO.getUrl(), tipoFuente);
        } else {
          throw new IllegalArgumentException("Tipo de fuente " + fuenteDTO.getTipoFuente() + " no soportado");
        }
        return fuente;
    }

  public FuenteDTOOutput fromEntity(Fuente f) {
    return new FuenteDTOOutput(f.getId(), f.getTipoFuente(), f.getUrl());
  }
}
