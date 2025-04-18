package ar.edu.utn.frba.dds.Entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ImportadorCSV implements ImportadorCSVAdapter{
    @Override
    public Set<Object> importar(String rutaArchivo, String separador) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        List<String[]> filas = new ArrayList<>(); // Lista para guardar cada fila

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea = "";
            while ((linea = br.readLine()) != null) {
                String[] datosFila = linea.split(separador);
                filas.add(datosFila); // Añadir la fila (arreglo) a la lista
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //devolver los hechos
        //TODO
        return Collections.singleton(filas);
    }
}