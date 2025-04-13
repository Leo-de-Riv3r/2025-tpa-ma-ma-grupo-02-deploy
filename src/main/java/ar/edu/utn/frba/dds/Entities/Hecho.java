package ar.edu.utn.frba.dds.Entities;

import ar.edu.utn.frba.dds.Enums.Origen;

import java.time.LocalDateTime;

public class Hecho {
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private Ubicacion ubicacion;
    private LocalDateTime fechaAcontecimiento;
    private LocalDateTime fechaCarga;
    private Origen origen;
    private Multimedia multimedia;
    private Boolean eliminado;

    public Boolean coincideTitulo(String titulo){
        return this.titulo.equals(titulo);
    }
}
