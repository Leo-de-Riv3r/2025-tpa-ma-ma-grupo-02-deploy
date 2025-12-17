package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechoRepository extends JpaRepository<Hecho, Long>, JpaSpecificationExecutor<Hecho> {

        @Query(value = "SELECT categoria " +
                        "FROM hecho " +
                        "WHERE MATCH(categoria, titulo) AGAINST (:categoria) >= 5 " +
                        "ORDER BY MATCH(categoria, titulo) AGAINST (:categoria) DESC LIMIT 1", nativeQuery = true)
        Optional<String> buscarCategoriaNormalizada(@Param("categoria") String categoria);

        Optional<Hecho> findByTituloAndDescripcionAndFechaAcontecimiento(String titulo, String descripcion,
                        LocalDateTime fechaAcontecimiento);

        Hecho findFirstByUbicacion_LatitudAndUbicacion_LongitudAndUbicacion_Lugar_ProvinciaIsNotNull(
                        Double latitud, Double longitud);

        @Query("SELECT h FROM Hecho h JOIN h.origen o WHERE o.autor = :autor AND FUNCTION('DATE', h.fechaAcontecimiento) = FUNCTION('DATE', :fecha)")
        List<Hecho> findPosiblesDuplicados(@Param("autor") String autor, @Param("fecha") LocalDateTime fecha);

        @Query("SELECT h FROM Hecho h " +
                        "WHERE h.id IN (" +
                        "    SELECT MIN(h2.id) " +
                        "    FROM Hecho h2 JOIN h2.fuentes f " +
                        "    WHERE f.id IN :fuenteIds " +
                        "    GROUP BY h2.titulo, h2.categoria, h2.descripcion, FUNCTION('DATE', h2.fechaAcontecimiento), "
                        +
                        "             h2.ubicacion.lugar.provincia, h2.ubicacion.lugar.municipio, h2.ubicacion.lugar.departamento "
                        +
                        "    HAVING COUNT(DISTINCT f.id) >= :minimoConsenso " +
                        ")")
        List<Hecho> findHechosConsensuados(
                        @Param("fuenteIds") List<String> fuenteIds,
                        @Param("minimoConsenso") Long minimoConsenso);

        @Query("SELECT h FROM Hecho h " +
                        "WHERE h.id IN (" +
                        "    SELECT MIN(h2.id) " +
                        "    FROM Hecho h2 JOIN h2.fuentes f " +
                        "    WHERE f.id IN :fuenteIds " +
                        "    GROUP BY h2.titulo " +
                        "    HAVING COUNT(DISTINCT f.id) >= :minimoConsenso " +
                        "       AND COUNT(DISTINCT h2.descripcion) = 1 " +
                        "       AND COUNT(DISTINCT h2.categoria) = 1 " + 
                        "       AND COUNT(DISTINCT FUNCTION('DATE', h2.fechaAcontecimiento)) = 1 " + 
                        "       AND COUNT(DISTINCT h2.ubicacion.lugar.provincia) = 1 " +
                        "       AND COUNT(DISTINCT h2.ubicacion.lugar.municipio) = 1 " +
                        ")")
        List<Hecho> findHechosConsensuadosEstricto(
                        @Param("fuenteIds") List<String> fuenteIds,
                        @Param("minimoConsenso") Long minimoConsenso);
}