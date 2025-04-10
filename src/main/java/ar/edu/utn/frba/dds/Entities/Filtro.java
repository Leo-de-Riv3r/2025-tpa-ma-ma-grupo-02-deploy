package ar.edu.utn.frba.dds.Entities;

import java.time.LocalDateTime;

public class Filtro {
    private Categoria categoria;
    private Zona zona;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public Boolean cumpleFiltro(Hecho hecho){
        //TODO
        return true;
    }
}
