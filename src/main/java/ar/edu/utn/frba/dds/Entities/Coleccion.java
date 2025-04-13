package ar.edu.utn.frba.dds.Entities;
import java.util.*;
public class Coleccion {
    private String titulo;
    private String descripcion;
    private Set<Hecho> hechos;
    private Filtro criterio;

    public Boolean agregarHechos(Hecho hecho) {
    //TODO
    //lo cambie porque solo puede agregar hechos individualmente
        this.hechos.add(hecho);
        return true;
    }

    public Boolean eliminarHechos(String tituloHecho){
        //TODO
        //seria medio raro que busque mas de un titulo, mejor seria buscar un titulo por cada llamada al metodo
        //agregue coincideTitulo() para evitar acoplamiento
        hechos.removeIf(hecho -> hecho.coincideTitulo(tituloHecho));
        return true;
    }
}
