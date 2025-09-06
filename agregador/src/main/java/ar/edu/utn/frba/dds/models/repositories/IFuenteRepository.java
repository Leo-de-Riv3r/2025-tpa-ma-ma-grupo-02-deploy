package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Fuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IFuenteRepository extends JpaRepository<Fuente, String> {
  @Modifying
  @Query(value = "DELETE FROM hecho_consensuado hc WHERE hc.hecho_id IN " +
      "(SELECT id from hecho h WHERE h.fuente_id IS NULL)", nativeQuery = true)
  void eliminarHechosObsoletosDeHechoConsenso();

  @Modifying
  @Query(value = "DELETE FROM hecho h WHERE H.fuente_id IS NULL", nativeQuery = true)
  void eliminarHechosObsoletos();

  @Modifying
  @Query("UPDATE Fuente f SET f.inactivo = 1 WHERE f.id = :fuenteId")
  void deleteById(@Param("fuenteId") Long idFuente);

}
