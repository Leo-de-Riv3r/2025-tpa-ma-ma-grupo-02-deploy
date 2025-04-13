package ar.edu.utn.frba.dds.Entities;

import java.time.LocalDateTime;

public class Filtro {
    private Categoria categoria;
    private Zona zona;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public Boolean cumpleFiltro(Hecho hecho){
        //TODO
        //categoria podria ser solo un string
        //falta zona
        this.categoria.equals(hecho.getCategoria());
        this.fechaInicio == hecho.getFechaInicio();
        this.fechaFin == hecho.getFechaFin();
        return true;
    }
}
