package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import java.util.List;

public interface IHechosRepository {
  public HechoDTO save(HechoDTO hecho);
  public void setHechos (List<HechoDTO> hechos);
  public List<HechoDTO> getHechos(Integer page, Integer perPage);
}
