package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechoRepository extends JpaRepository<Hecho, Long> {
  //List<Hecho> busquedaTexto(String textoTitulo);
  //logica para normalizar categoria
  @Query(value =
    "SELECT categoria " +
    "FROM hecho " +
    "WHERE MATCH(categoria, titulo) AGAINST (:categoria) >= 5 "+
    "ORDER BY MATCH(categoria, titulo) AGAINST (:categoria) DESC LIMIT 1"
    , nativeQuery = true)
  Optional<String> buscarCategoriaNormalizada(@Param("categoria") String categoria);

  Optional<Hecho> findByTituloAndDescripcionAndFechaAcontecimiento(String titulo, String descripcion, LocalDateTime fechaAcontecimiento);
}
