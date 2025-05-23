package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Categoria;
import ar.edu.utn.frba.dds.models.repositories.ICategoriaRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository implements ICategoriaRepository {
    private List<Categoria> categorias;
    private Long nextId;

    public CategoriaRepository() {
        this.categorias = new ArrayList<>();
        this.nextId = 0L;
    }

    @Override
    public Categoria save(Categoria categoria) {
        categoria.setId(nextId);
        this.categorias.add(categoria);
        this.nextId++;
        return categoria;
    }

    @Override
    public Categoria findByNombre(String nombre) {
        for (Categoria categoria : categorias) {
            if (categoria.getNombre().equals(nombre)) {
                return categoria;
            }
        }
        return null;
    }
}
