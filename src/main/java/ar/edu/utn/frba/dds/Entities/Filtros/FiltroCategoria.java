package ar.edu.utn.frba.dds.Entities.Filtros;

import ar.edu.utn.frba.dds.Entities.Hecho;

public class FiltroCategoria implements IFiltroAdapter {
    private String categoria;

    public FiltroCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public Boolean cumpleFiltro(Hecho hecho) {
        return hecho.getCategoria().getNombre() == categoria;
    }
}
