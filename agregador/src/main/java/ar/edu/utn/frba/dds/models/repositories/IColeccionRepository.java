package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTO;
import ar.edu.utn.frba.dds.models.entities.Coleccion;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.IFiltroStrategy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionRepository extends JpaRepository<Coleccion, String> {

}
