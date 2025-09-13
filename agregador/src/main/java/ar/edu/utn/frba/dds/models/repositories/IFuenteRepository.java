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
  @Query(value = "DELETE FROM hecho h WHERE H.fuente_id IS NULL", nativeQuery = true)
  void eliminarHechosObsoletos();

  @Modifying
  @Query("UPDATE Fuente SET inactivo = 1 WHERE id = :idFuente")
  void deleteById(@Param("idFuente") String idFuente);

  boolean existsByUrl(String url);


}
