package ar.edu.utn.frba.dds.models.entities.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;

public interface IHechosRepository {
  public void upload(Hecho hecho);
  public void modify(Hecho hecho);
}
