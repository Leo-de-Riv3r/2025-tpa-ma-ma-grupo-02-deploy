package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import java.util.List;

public interface IFuenteEstaticaService {

  List<HechoDTO> getHechos(Integer page, Integer perPage);
  public void extraeryGuardarHechos(String urlCsv, String separador);
}
