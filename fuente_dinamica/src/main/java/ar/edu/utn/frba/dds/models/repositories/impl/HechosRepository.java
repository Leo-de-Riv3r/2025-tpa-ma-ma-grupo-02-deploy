package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HechosRepository implements IHechosRepository {
  private List<Hecho> hechos;
  private Long nextId;

  public HechosRepository(){
    this.hechos = new ArrayList<>();
    this.nextId = 0L;
  }

  @Override
  public Hecho save(Hecho hecho) {
    hecho.setId((this.nextId));
    hecho.setFechaCarga(LocalDateTime.now());
    this.hechos.add(hecho);
    this.nextId++;
    return hecho;
  }

  @Override
  public Hecho findById(Long id) {
    Hecho hecho = null;
    for (Hecho h: hechos) {
      if (h.getId().equals(id)) {
        return h;
      }
    }
    return hecho;
  }

  @Override
  public void delete(Hecho hecho) {
    this.hechos.remove(hecho);
  }

  @Override
  public void deleteById(Long id) {
    this.hechos = this.hechos.stream().filter(hecho -> !hecho.getId().equals(id)).toList();
  }

  @Override
  public long count() {
    return hechos.size();
  }
}
