package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.models.dtos.output.HechoPagDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.enums.EstadoHecho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {

  List<Hecho> findByEstadoHechoIn(List<EstadoHecho> estados);

  List<Hecho> findByEstadoHecho(EstadoHecho estadoHecho);

  @Query("SELECT h FROM Hecho WHERE h.estadoHecho = 'ACEPTADO' or h.estadoHecho = 'ACEPTADO_CON_SUGERENCIAS'")
  List<Hecho> findHechosAceptados();

  @Query("SELECT h FROM Hecho h WHERE h.estadoHecho = 'PENDIENTE'")
  List<Hecho> findHechosPendientes();

  @Query("SELECT h from Hecho h WHERE h.estadoHecho = 'RECHAZADO'")
  List<Hecho> findHechosRechazados();
}
