package ar.edu.utn.frba.dds.Entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportadorHecho {
    private String rutaArchivo;
    private String separadorColumna;

    public Boolean cargarColeccion(Coleccion coleccion) {
       //TODO
        List<Hecho> hechosImportador = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean esPrimera = true;

            while ((linea = br.readLine()) != null) {
                // Omitimos la cabecera
                if (esPrimera) {
                    esPrimera = false;
                    continue;
                }

//                String[] campos = linea.split(",");
//
//                String nombre = campos[0].trim();
//                int edad = Integer.parseInt(campos[1].trim());
//                String ciudad = campos[2].trim();
//
//                Persona persona = new Persona(nombre, edad, ciudad);
//                personas.add(persona);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
