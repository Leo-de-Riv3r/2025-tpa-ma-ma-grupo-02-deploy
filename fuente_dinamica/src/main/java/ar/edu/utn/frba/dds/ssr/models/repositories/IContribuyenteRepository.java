package ar.edu.utn.frba.dds.ssr.models.repositories;

import ar.edu.utn.frba.dds.ssr.models.entities.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContribuyenteRepository extends JpaRepository<Contribuyente, Long> {
    Contribuyente findByEmail(String email);
    void delete(Contribuyente contribuyente);
    void deleteById(Long id);
}
