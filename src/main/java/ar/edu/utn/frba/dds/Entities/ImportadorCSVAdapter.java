package ar.edu.utn.frba.dds.Entities;

import java.util.Set;

public interface ImportadorCSVAdapter {
    public Set<Object> importar(String rutaArchivo, String separador);
}