package ar.edu.utn.frba.dds.Entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImportadorHecho {
    private String rutaArchivo;
    private String separadorColumna;

    public Boolean cargarColeccion(Coleccion coleccion) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            List<String[]> filas = new ArrayList<>(); // Lista para guardar cada fila

            try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea = "";
                while ((linea = br.readLine()) != null) {
                    String[] datosFila = linea.split(separadorColumna);
                    filas.add(datosFila); // Añadir la fila (arreglo) a la lista
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Cargar hechos en la coleccion
            for (String[] fila : filas) {
                String titulo = fila[0];
                String descripcion = fila[1];
                Categoria categoria = fila[2];
                Integer latitud = Integer.valueOf(fila[3]);
                Integer longitud = Integer.valueOf(fila[4]);
                Ubicacion ubicacion = new Ubicacion(latitud, longitud);
                LocalDateTime fechaAcontecimiento = LocalDateTime.parse(fila[5]);
                LocalDateTime fechaCarga = LocalDateTime.now();
                //TODO falta los otros datos y probar si carga bien las filas
                coleccion.agregarHechos(new Hecho(titulo, descripcion, categoria, ubicacion, fechaAcontecimiento, fechaCarga));
            }
    }
}
