package ar.edu.utn.frba.dds.Entities;

import ar.edu.utn.frba.dds.Enums.Origen;

import java.time.LocalDateTime;
import java.util.Set;

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
    private Set<String> etiquetas;
    public Boolean coincideTitulo(String titulo){
        return this.titulo.equals(titulo);
    }
    public void agregarEtiqueta(String etiqueta){
        this.etiquetas.add(etiqueta);
    }
    public String getTitulo() {
        return titulo;
    }

    public LocalDateTime getFechaAcontecimiento() {
        return fechaAcontecimiento;
    }

    public Ubicacion getUbicacion(){
        return this.ubicacion;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }
}
