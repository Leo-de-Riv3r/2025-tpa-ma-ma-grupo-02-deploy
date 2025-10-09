package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.enums.Formato;
import ar.edu.utn.frba.dds.services.IMultimediaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class MultimediaService implements IMultimediaService {
    @Value("${app.multimedia.upload-dir}")
    private String uploadDir;

    @Value("${app.multimedia.base-path}")
    private String basePath;

    @Override
    public Multimedia guardarArchivo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo multimedia está vacío.");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Formato formato = determinarFormato(extension);
        String rutaAcceso = basePath + "/" + uniqueFilename;

        return Multimedia.builder()
                .nombre(originalFilename)
                .ruta(rutaAcceso)
                .formato(formato)
                .build();
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    private Formato determinarFormato(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif", "bmp" -> Formato.IMAGEN;
            case "mp4", "avi", "mov", "wmv" -> Formato.VIDEO;
            case "mp3", "wav", "ogg" -> Formato.AUDIO;
            case "txt", "pdf", "doc", "docx" -> Formato.TEXTO;
            default -> throw new IllegalArgumentException("Extensión de archivo no soportada: " + extension);
        };
    }
}