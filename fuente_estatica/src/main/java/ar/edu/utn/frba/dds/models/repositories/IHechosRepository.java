package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import ar.edu.utn.frba.dds.models.DTO.HechosPagDTO;
import java.util.List;

public interface IHechosRepository {
  public HechoDTO save(HechoDTO hecho);
  public void setHechos (List<HechoDTO> hechos);
  public HechosPagDTO getHechos(Integer page, Integer perPage);
}
