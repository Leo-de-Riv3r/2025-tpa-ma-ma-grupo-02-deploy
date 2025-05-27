package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Contribuyente;
import ar.edu.utn.frba.dds.models.repositories.IContribuyenteRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ContribuyenteRepository implements IContribuyenteRepository {
    private List<Contribuyente> contribuyentes;
    private Long nextId;

    public ContribuyenteRepository() {
        this.contribuyentes = new ArrayList<>();
        this.nextId = 0L;
    }

    @Override
    public void save(Contribuyente contribuyente) {
        contribuyente.setId(nextId);
        this.contribuyentes.add(contribuyente);
        this.nextId++;
    }

    @Override
    public Contribuyente findById(Long id) {
        for (Contribuyente c : this.contribuyentes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public Contribuyente findByEmail(String email) {
        for (Contribuyente c : this.contribuyentes) {
            if (c.getEmail().equals(email)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void delete(Contribuyente contribuyente) {
        this.contribuyentes.remove(contribuyente);
    }

    @Override
    public void deleteById(Long id) {
        this.contribuyentes = this.contribuyentes.stream().filter(c -> !c.getId().equals(id)).toList();
    }
}
