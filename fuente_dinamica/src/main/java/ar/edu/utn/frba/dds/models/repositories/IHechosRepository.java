package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.List;

public interface IHechosRepository {
  public Hecho save(Hecho hecho);
  public Hecho findById(Long id);
  public void delete(Hecho hecho);
  public void deleteById(Long id);
  public long count();
  public List<Hecho> findAll();
}
