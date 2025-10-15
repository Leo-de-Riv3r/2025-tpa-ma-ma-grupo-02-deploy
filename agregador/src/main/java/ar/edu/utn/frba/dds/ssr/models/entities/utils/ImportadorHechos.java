package ar.edu.utn.frba.dds.ssr.models.entities.utils;

import ar.edu.utn.frba.dds.ssr.models.entities.adapters.CsvReaderAdapter;
import ar.edu.utn.frba.dds.ssr.models.entities.Hecho;
import java.util.Set;

public class ImportadorHechos {
  private CsvReaderAdapter csvReader;
  private String rutaArchivo;
  private String separadorColumna;
  private String urlFuente;

  public Set<Hecho> importarHechos() {
    Object hechosCrudos = this.csvReader.readCsv(rutaArchivo, separadorColumna);
    //TODO: Implementar mapeo de objeto a clase Hecho.
    return Set.of();
  }
}