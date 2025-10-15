package ar.edu.utn.frba.dds.ssr.models.repositories;

import ar.edu.utn.frba.dds.ssr.models.entities.Fuente;
import ar.edu.utn.frba.dds.ssr.models.entities.enums.TipoFuente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFuenteRepository extends JpaRepository<Fuente, String> {
  Optional<Fuente> findByUrlAndTipoFuente(String url, TipoFuente tipoFuente);
}
