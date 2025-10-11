package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ISolicitudRepository extends JpaRepository<Solicitud, Long> {
  @Query("SELECT s FROM Solicitud s WHERE s.estadoActual.estado = :tipoEstado")
  Page<Solicitud> findByEstadoActual(TipoEstado tipoEstado, Pageable pageable);
}
