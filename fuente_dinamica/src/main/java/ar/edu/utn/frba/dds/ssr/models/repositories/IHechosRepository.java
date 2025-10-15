package ar.edu.utn.frba.dds.ssr.models.repositories;

import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import ar.edu.utn.frba.dds.ssr.models.enums.EstadoHecho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {

  List<Hecho> findByEstadoHechoIn(List<EstadoHecho> estados);

  List<Hecho> findByEstadoHecho(EstadoHecho estadoHecho);

  @Query(value = "SELECT * FROM hechos WHERE estado = 'PENDIENTE'", nativeQuery = true)
  List<Hecho> findHechosPendientes();

  @Query(value = "SELECT * FROM hechos WHERE estado = 'RECHAZADO'", nativeQuery = true)
  List<Hecho> findHechosRechazados();

  @Query(value = "SELECT * FROM hechos WHERE estado IN ('ACEPTADO', 'ACEPTADO_CON_SUGERENCIAS')", nativeQuery = true)
  List<Hecho> findHechosAceptados();
}
