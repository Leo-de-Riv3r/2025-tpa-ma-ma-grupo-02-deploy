package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface IMultimediaService {
    Multimedia guardarArchivo(MultipartFile file) throws IOException;
    InputStream obtenerArchivo(Long id) throws IOException;
    void eliminarArchivo(Long id) throws IOException;
    boolean existeArchivo(Long id);
}