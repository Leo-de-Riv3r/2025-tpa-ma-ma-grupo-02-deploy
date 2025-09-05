package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: Agregar metodos faltantes!
public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria save(Categoria categoria);
    Categoria findByNombre(String nombre);
}
