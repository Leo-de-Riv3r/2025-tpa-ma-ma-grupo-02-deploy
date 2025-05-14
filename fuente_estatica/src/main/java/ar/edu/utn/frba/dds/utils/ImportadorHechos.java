package ar.edu.utn.frba.dds.utils;

import ar.edu.utn.frba.dds.adapters.CsvReaderAdapter;
import ar.edu.utn.frba.dds.entities.Hecho;
import java.util.Set;

public class ImportadorHechos {
  private CsvReaderAdapter csvReader;
  private String rutaArchivo;
  private String separadorColumna;

  public Set<Hecho> importarHechos() {
    Object hechosCrudos = this.csvReader.readCsv(rutaArchivo, separadorColumna);
    //TODO: Implementar mapeo de objeto a clase Hecho.
    return Set.of();
  }
}