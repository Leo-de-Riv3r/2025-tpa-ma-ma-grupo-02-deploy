package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PrimerEscenarioTest {
    private Set<Hecho> hechos;
    private Coleccion coleccion;

    @BeforeEach
    public void setUp() {
        hechos = new HashSet<>();
        hechos.add(Hecho.builder()
                .titulo("Caída de aeronave impacta en Olavarría")
                .descripcion("Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.")
                .categoria(new Categoria("Caída de aeronave"))
                .ubicacion(new Ubicacion(-36.868375, -60.343297))
                .fechaAcontecimiento(LocalDateTime.of(2001, 11, 29, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Serio incidente: Accidente con maquinaria industrial en Chos Malal, Neuquén")
                .descripcion("Un grave accidente con maquinaria industrial se registró en Chos Malal, Neuquén. El incidente dejó a varios sectores sin comunicación. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.")
                .categoria(new Categoria("Accidente con maquinaria industrial"))
                .ubicacion(new Ubicacion(-37.345571, -70.241485))
                .fechaAcontecimiento(LocalDateTime.of(2001, 8, 16, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Caída de aeronave impacta en Venado Tuerto, Santa Fe")
                .descripcion("Grave caída de aeronave ocurrió en las inmediaciones de Venado Tuerto, Santa Fe. El incidente destruyó viviendas y dejó a familias evacuadas. Autoridades nacionales se han puesto a disposición para brindar asistencia.")
                .categoria(new Categoria("Caída de aeronave"))
                .ubicacion(new Ubicacion(-33.768051, -61.921032))
                .fechaAcontecimiento(LocalDateTime.of(2008, 8, 8, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Accidente en paso a nivel deja múltiples daños en Pehuajó, Buenos Aires")
                .descripcion("Grave accidente en paso a nivel ocurrió en las inmediaciones de Pehuajó, Buenos Aires. El incidente generó preocupación entre las autoridades provinciales. El Ministerio de Desarrollo Social está brindando apoyo a los damnificados.")
                .categoria(new Categoria("Accidente en paso a nivel"))
                .ubicacion(new Ubicacion(-35.855811, -61.940589))
                .fechaAcontecimiento(LocalDateTime.of(2020, 1, 27, 0, 0))
                .build());
        hechos.add(Hecho.builder()
                .titulo("Devastador Derrumbe en obra en construcción afecta a Presidencia Roque Sáenz Peña")
                .descripcion("Un grave derrumbe en obra en construcción se registró en Presidencia Roque Sáenz Peña, Chaco. El incidente generó preocupación entre las autoridades provinciales. El intendente local se ha trasladado al lugar para supervisar las operaciones.")
                .categoria(new Categoria("Derrumbe en obra en construcción"))
                .ubicacion(new Ubicacion(-26.780008, -60.458782))
                .fechaAcontecimiento(LocalDateTime.of(2016, 6, 4, 0, 0))
                .build());

        coleccion = new Coleccion("Colección prueba", "Esto es una prueba");

        coleccion.setHechos(hechos); // Asociar la colección con la fuente (?

    }

    @Test
    public void criteriosDePertenenciaTest() {
        Filtro filtro1 = Filtro.builder()
                .fechaInicio(LocalDate.of(2000, 1, 1))
                .fechaFin(LocalDate.of(2010, 1, 1))
                .build();

        coleccion.agregarCriterio(filtro1);

        Assertions.assertEquals(3, coleccion.getHechos().size());

        Filtro filtro2 = Filtro.builder()
                .categoria(new Categoria("Caída de aeronave"))
                .build();

        coleccion.agregarCriterio(filtro2);

        Assertions.assertEquals(2, coleccion.getHechos().size());
    }

}
