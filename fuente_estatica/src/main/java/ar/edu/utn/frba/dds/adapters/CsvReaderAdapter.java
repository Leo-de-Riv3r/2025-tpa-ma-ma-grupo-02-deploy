package ar.edu.utn.frba.dds.adapters;

import java.util.List;

public interface CsvReaderAdapter {
  public List<Object> readCsv(String path, String separator);
}