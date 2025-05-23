package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Contribuyente;
import ar.edu.utn.frba.dds.models.entities.Hecho;

public interface IContribuyenteRepository {
    public void save(Contribuyente contribuyente);
    public Contribuyente findById(Long id);
    public Contribuyente findByEmail(String email);
    public void delete(Contribuyente contribuyente);
    public void deleteById(Long id);
}
