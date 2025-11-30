package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.entities.SolicitudModificacionHecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ISolicitudesModificacionRepository extends JpaRepository<SolicitudModificacionHecho, Long> {
  @Query("SELECT s FROM SolicitudModificacionHecho s WHERE s.estadoSolicitudModificacion = 'PENDIENTE'")
  Page<SolicitudModificacionHecho> findPendientes(Pageable pageable);
}
