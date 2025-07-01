package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.entities.Categoria;
import ar.edu.utn.frba.dds.models.entities.Origen;
import ar.edu.utn.frba.dds.models.entities.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.adapters.CsvReaderAdapter;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.enums.TipoOrigen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
public class ImportadorHechos {
  private CsvReaderAdapter csvReader;
  private String rutaArchivo;
  private String separadorColumna;

  public Set<Hecho> importarHechos() {
    List<Object> filas = this.csvReader.readCsv(rutaArchivo, separadorColumna);
    Set<Hecho> hechos = new HashSet<>();

    for (Object o : filas) {
      HechoCsv dto = (HechoCsv) o;

      Hecho hecho = Hecho.builder()
              .titulo(dto.getTitulo())
              .descripcion(dto.getDescripcion())
              .categoria(new Categoria(dto.getCategoria()))
              .ubicacion(new Ubicacion(dto.getLatitud(), dto.getLongitud()))
              .fechaAcontecimiento(dto.getFecha().atStartOfDay())
              .fechaCarga(LocalDateTime.now())
              .origen(new Origen(TipoOrigen.DATASET, "nombre autor")) // ???
              .build();

      hechos.add(hecho);
    }
    return hechos;
  }
}