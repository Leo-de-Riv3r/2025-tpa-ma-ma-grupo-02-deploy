package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.dtos.FuenteDTO;
import ar.edu.utn.frba.dds.models.dtos.output.FuenteDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.FuenteDefault;
import ar.edu.utn.frba.dds.models.entities.FuenteProxyMetamapa;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import jakarta.persistence.EntityExistsException;

public class FuenteConverter {
  public static Fuente fromDto(FuenteDTO fuenteDTO) {
    if (fuenteDTO.getTipoFuente() == null || fuenteDTO.getUrl() == null){
      throw new IllegalArgumentException("La url y/o el tipo de fuente estan vacios");
    }
        TipoFuente tipoFuente = TipoFuente.valueOf(fuenteDTO.getTipoFuente().toUpperCase());
        Fuente fuente;
        if (tipoFuente == TipoFuente.PROXY_METAMAPA) {
          fuente = new FuenteProxyMetamapa(fuenteDTO.getUrl());
          //fuente.cambiarHechos(fuente.getHechos());
        } else if (tipoFuente == TipoFuente.DINAMICA || tipoFuente == TipoFuente.ESTATICA) {
          fuente = new FuenteDefault(fuenteDTO.getUrl(), tipoFuente);
          fuente.refrescarHechos();
        } else {
          throw new IllegalArgumentException("Tipo de fuente " + fuenteDTO.getTipoFuente() + " no soportado");
        }
        return fuente;
    }
  }
