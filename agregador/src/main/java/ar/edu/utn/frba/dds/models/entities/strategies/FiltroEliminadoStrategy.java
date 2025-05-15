package ar.edu.utn.frba.dds.models.entities.strategies;

import ar.edu.utn.frba.dds.entities.Hecho;

import java.util.HashSet;
import java.util.Set;

public class FiltroEliminadoStrategy implements FiltroStrategy {
    private Set<Hecho> eliminados;

    public FiltroEliminadoStrategy() {
        this.eliminados = new HashSet<>();
    }

    @Override
    public boolean cumpleFiltro(Hecho hecho) {
        return !this.eliminados.contains(hecho);
    }

    public void agregarEliminado(Hecho hecho) {
        this.eliminados.add(hecho);
    }
}
