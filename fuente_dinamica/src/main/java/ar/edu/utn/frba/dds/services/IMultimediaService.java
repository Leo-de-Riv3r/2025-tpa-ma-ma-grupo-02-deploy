package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IMultimediaService {
    Multimedia guardarArchivo(MultipartFile file) throws IOException;
}