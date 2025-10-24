package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionRepository extends JpaRepository<Coleccion, String> {

}
