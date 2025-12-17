package ar.edu.utn.frba.dds.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DatabaseInitializerConfig {

  @Bean
  public ApplicationRunner createIndexes(JdbcTemplate jdbcTemplate) {
    return args -> {
      try {
        // 1. Índice FULLTEXT (Existente)
        String checkFullTextSql = "SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS " +
            "WHERE table_schema = DATABASE() AND table_name = 'hecho' " +
            "AND (index_name = 'idx_hecho_titulo_categoria' OR index_name = 'idx_hecho_categoria_titulo')";

        Integer fullTextExists = jdbcTemplate.queryForObject(checkFullTextSql, Integer.class);

        if (fullTextExists != null && fullTextExists == 0) {
          log.info("Creando índice FULLTEXT: idx_hecho_categoria_titulo");
          String createIndexSql = "CREATE FULLTEXT INDEX idx_hecho_categoria_titulo ON hecho(titulo, categoria)";
          jdbcTemplate.execute(createIndexSql);
        }

        String checkGroupIndexSql = "SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS " +
            "WHERE table_schema = DATABASE() AND table_name = 'hecho' " +
            "AND index_name = 'idx_hecho_agrupacion'";

        Integer groupIndexExists = jdbcTemplate.queryForObject(checkGroupIndexSql, Integer.class);

        if (groupIndexExists != null && groupIndexExists == 0) {
          log.info("Creando índice de AGRUPACIÓN: idx_hecho_agrupacion");
          String createGroupIndexSql = "CREATE INDEX idx_hecho_agrupacion ON hecho(titulo(100), categoria, descripcion(300), fecha_acontecimiento)";
          jdbcTemplate.execute(createGroupIndexSql);
        }

      } catch (Exception e) {
        log.error("Error al verificar/crear índices en la base de datos: {}", e.getMessage());
      }
    };
  }
}