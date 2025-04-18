package ar.edu.utn.frba.dds.Entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImportadorDeHecho {
    private String rutaArchivo;
    private String separadorColumna;
    private ImportadorCSVAdapter importadorCSV;
    public Set<Hecho> importar() {
        return this.importadorCSV.importar(rutaArchivo, separadorColumna);
    }
}