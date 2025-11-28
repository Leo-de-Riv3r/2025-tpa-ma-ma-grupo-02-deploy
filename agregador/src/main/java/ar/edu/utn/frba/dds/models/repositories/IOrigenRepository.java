package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.enums.TipoFuente;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrigenRepository extends JpaRepository<Origen, Long> {
  Optional<Origen> findByTipoAndAutor(TipoFuente tipo, String autor);
}
