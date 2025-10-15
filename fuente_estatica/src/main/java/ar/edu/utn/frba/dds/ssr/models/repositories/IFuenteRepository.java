package ar.edu.utn.frba.dds.ssr.models.repositories;

import ar.edu.utn.frba.dds.ssr.models.entities.Fuente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFuenteRepository extends JpaRepository<Fuente, Long> {
}
