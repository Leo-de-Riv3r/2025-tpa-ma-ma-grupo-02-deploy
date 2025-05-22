package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;

public interface IHechosRepository {
  public Hecho save(Hecho hecho);
  public Hecho findById(Long id);
  public void delete(Hecho hecho);
  public void deleteById(Long id);
  public long count();

}
