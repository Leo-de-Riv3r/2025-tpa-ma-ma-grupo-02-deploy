package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  List<Hecho> findByTituloAndDescripcionAndFechaAcontecimiento(String titulo, String descripcion, LocalDateTime fechaAcontecimiento);

  @Query("""
        SELECT h FROM Hecho h
        WHERE CONCAT(h.titulo, '|', h.descripcion, '|', h.fechaAcontecimiento) IN :claves
    """)
  List<Hecho> buscarPorClaves(@Param("claves") Set<String> claves);

  @Query("SELECT DISTINCT h.ubicacion.lugar.provincia FROM Hecho h WHERE h.ubicacion.lugar.provincia IS NOT NULL AND h.ubicacion.lugar.provincia != '' ORDER BY h.ubicacion.lugar.provincia ASC")
  List<String> findProvinciasDisponibles();

  @Query("SELECT DISTINCT h.ubicacion.lugar.municipio FROM Hecho h WHERE h.ubicacion.lugar.municipio IS NOT NULL AND h.ubicacion.lugar.municipio != '' ORDER BY h.ubicacion.lugar.municipio ASC")
  List<String> findMunicipiosDisponibles();

  @Query("SELECT DISTINCT h.ubicacion.lugar.departamento FROM Hecho h WHERE h.ubicacion.lugar.departamento IS NOT NULL AND h.ubicacion.lugar.departamento != '' ORDER BY h.ubicacion.lugar.departamento ASC")
  List<String> findDepartamentosDisponibles();

  Hecho findFirstByUbicacion_LatitudAndUbicacion_LongitudAndUbicacion_Lugar_ProvinciaIsNotNull(
      Double latitud, Double longitud);

  @Query("SELECT h FROM Hecho h JOIN h.origen o WHERE o.autor = :autor AND h.fechaAcontecimiento = :fecha")
  List<Hecho> findPosiblesDuplicados(@Param("autor") String autor, @Param("fecha") LocalDateTime fecha);
}
