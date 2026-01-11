package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.Multimedia;
import ar.edu.utn.frba.dds.models.repositories.IMultimediaJpaRepository;
import ar.edu.utn.frba.dds.models.repositories.IMultimediaRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@Service
public class MultimediaService {

    private final IMultimediaRepository multimediaRepository;
    private final IMultimediaJpaRepository multimediaJpaRepository;
    private final S3Service s3Service;
    @Value("${spring.destination.folder}")
    private String destinationFolder;

    public MultimediaService(IMultimediaRepository multimediaRepository,
            IMultimediaJpaRepository multimediaJpaRepository, S3Service s3Service) {
        this.multimediaRepository = multimediaRepository;
        this.multimediaJpaRepository = multimediaJpaRepository;
        this.s3Service = s3Service;
    }

    public Multimedia guardarArchivo(MultipartFile file) throws IOException {

        Path staticDir = Paths.get(destinationFolder);
        if (!Files.exists(staticDir)) {
            Files.createDirectories(staticDir);
        }

        Path filePath = staticDir.resolve(file.getOriginalFilename());
        Path finalPath = Files.write(filePath, file.getBytes());

        String urlAccesoS3 = this.s3Service.uploadFile("metamapa-files", file.getOriginalFilename(), finalPath);
        Files.delete(filePath);
        Multimedia multimedia = multimediaRepository.guardar(file, urlAccesoS3);
        return multimediaJpaRepository.save(multimedia);
    }

    public InputStream obtenerArchivo(Long id) throws IOException {
        Multimedia multimedia = multimediaJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe multimedia con id " + id));

        String nombreArchivo = Paths.get(multimedia.getRuta()).getFileName().toString();
        return multimediaRepository.obtener(nombreArchivo);
    }

    public void eliminarArchivo(Long id) throws IOException {
        Multimedia multimedia = multimediaJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe multimedia con id " + id));

        String nombreArchivo = Paths.get(multimedia.getRuta()).getFileName().toString();
        multimediaRepository.eliminar(nombreArchivo);
        multimediaJpaRepository.delete(multimedia);
    }

    public boolean existeArchivo(Long id) {
        return multimediaJpaRepository.existsById(id);
    }
}