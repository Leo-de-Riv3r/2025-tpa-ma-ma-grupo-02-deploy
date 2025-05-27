package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.DTO.HechosPagDTO;

public interface IFuenteEstaticaService {

  HechosPagDTO getHechos(Integer page, Integer perPage);
  public void extraeryGuardarHechos(String urlCsv, String separador);
}
