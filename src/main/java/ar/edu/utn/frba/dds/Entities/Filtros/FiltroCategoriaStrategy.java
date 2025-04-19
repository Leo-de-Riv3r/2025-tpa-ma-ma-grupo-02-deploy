package ar.edu.utn.frba.dds.Entities.Filtros;

import ar.edu.utn.frba.dds.Entities.Hecho;

public class FiltroCategoriaStrategy implements FiltroStrategy {
    private String categoria;

    public FiltroCategoriaStrategy(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public Boolean cumpleFiltro(Hecho hecho) {
        return hecho.getCategoria().getNombre() == categoria;
    }
}
