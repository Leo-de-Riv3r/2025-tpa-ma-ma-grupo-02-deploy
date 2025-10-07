package ar.edu.utn.frba.dds.estadisticas.models.repositories;

import ar.edu.utn.frba.dds.estadisticas.models.entities.Estadistica;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryEstadisticas extends JpaRepository<Estadistica, Long> {
}
