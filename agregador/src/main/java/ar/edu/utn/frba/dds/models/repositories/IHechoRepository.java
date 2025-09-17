package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechoRepository extends JpaRepository<Hecho, Long> {
  boolean existsByTituloAndDescripcionAndCategoria(String titulo, String descripcion, String categoria);

  //List<Hecho> busquedaTexto(String textoTitulo);
  //logica para normalizar categoria
  @Query(value =
    "SELECT categoria" +
    "FROM hecho" +
    "WHERE MATCH(categoria, titulo) AGAINST (:categoria) >= 5"+
    "ORDER BY MATCH(categoria, titulo) AGAINST (:categoria) DESC LIMIT 1"
    , nativeQuery = true)
  Optional<String> buscarCategoriaNormalizada(@Param("categoria") String categoria);
}
