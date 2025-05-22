package ar.edu.utn.frba.dds.models.entities.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.repositories.IHechosRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HechosRepository implements IHechosRepository {
  private List<Hecho> hechos;

  public HechosRepository() {
    this.hechos = new ArrayList<>();
  }

  @Override
  public void upload(Hecho hecho) {
    this.hechos.add(hecho);
  }

  @Override
  public void modify(Hecho hecho) {

  }
}
