package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Categoria;

// TODO: Agregar metodos faltantes!
public interface ICategoriaRepository {
    public Categoria save(Categoria categoria);
    public Categoria findByNombre(String nombre);
}
