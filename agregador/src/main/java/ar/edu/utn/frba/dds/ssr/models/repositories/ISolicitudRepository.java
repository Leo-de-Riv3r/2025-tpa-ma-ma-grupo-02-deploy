package ar.edu.utn.frba.dds.ssr.models.repositories;

import ar.edu.utn.frba.dds.ssr.models.entities.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudRepository extends JpaRepository<Solicitud, Long> {

}
