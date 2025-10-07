package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
}
