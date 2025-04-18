package ar.edu.utn.frba.dds.Entities;
import ar.edu.utn.frba.dds.Entities.Filtros.IFiltroAdapter;

import java.util.*;

public class Coleccion {
    private String titulo;
    private String descripcion;
    private Set<Hecho> hechos;
    private Set<IFiltroAdapter> criterios;

    public Coleccion(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Boolean agregarHechos(Hecho nuevoHecho) {
    //TODO
    //lo cambie porque solo puede agregar hechos individualmente
        Boolean repetido = this.hechos.stream()
                .anyMatch(hecho -> Objects.equals(hecho.getTitulo(), nuevoHecho.getTitulo()));

        if(repetido){
            this.eliminarHechos(nuevoHecho.getTitulo());
        }
        this.hechos.add(nuevoHecho);
        return true;
    }


    public Boolean eliminarHechos(String tituloHecho){
        //TODO
        //seria medio raro que busque mas de un titulo, mejor seria buscar un titulo por cada llamada al metodo
        hechos.removeIf(hecho -> Objects.equals(hecho.getTitulo(), tituloHecho));
        return true;
    }

    public void agregarCriterio(IFiltroAdapter filtro) {
        this.criterios.add(filtro);
        this.filtrarHechos();
    }

    public void filtrarHechos(){
        //TODO filtrar hechos que cumplan todos los criterios
        this.hechos = (Set<Hecho>) this.hechos.stream().filter(hecho -> this.cumpleCriterios(hecho));
    }

    private boolean cumpleCriterios(Hecho hecho) {
        return criterios.stream().anyMatch(criterio -> !(criterio.cumpleFiltro(hecho)));
    }
}
