package ar.edu.utn.frba.dds.Entities;
import java.util.*;
public class Coleccion {
    private String titulo;
    private String descripcion;
    private Set<Hecho> hechos;
    private Filtro criterio;

    public Boolean agregarHechos(Set<Hecho> hechos) {
    //TODO
    //tira error porque solo puede agregar hechos individualmente
        this.hechos.add(hechos);
        return true;
    }

    public Boolean eliminarHechos(List<String> hechos){
        //TODO
        //seria mejor este metodo para evitar acoplamiento
        hechos.removeIf(hecho -> hechos.coincideTitulo(titulo));
        return true;
    }
}
