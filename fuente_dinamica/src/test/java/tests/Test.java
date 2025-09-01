package tests;
import ar.edu.utn.frba.dds.models.entities.Categoria;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.repositories.impl.HechosRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.*;
public class Test {
  @BeforeEach
  public void cargarDatos(){
    HechosRepository hechosRepo = new HechosRepository();
    Hecho hecho1 = Hecho.builder()
        .titulo("Hecho prueba 1")
        .descripcion("hechos para test")
        .categoria(new Categoria("prueba"))
        .ubicacion(new Ubicacion(12.902, 23.6))
        .fechaAcontecimiento(LocalDateTime.of(2025, 3, 10, 10, 10, 10))
        .fechaCarga(LocalDateTime.of(2025, 3, 20, 9, 2, 10))
        .build();
    Hecho hecho2 = Hecho.builder()
        .titulo("Hecho prueba 2")
        .descripcion("hechos para test")
        .categoria(new Categoria("prueba"))
        .ubicacion(new Ubicacion(22.3, 756.2223))
        .fechaAcontecimiento(LocalDateTime.of(2025, 1, 20, 10, 10, 10))
        .fechaCarga(LocalDateTime.of(2025, 1, 22, 9, 2, 10))
        .build();
    hechosRepo.save(hecho1);
    hechosRepo.save(hecho2);
    //hechosRepo.save();

  }
}
