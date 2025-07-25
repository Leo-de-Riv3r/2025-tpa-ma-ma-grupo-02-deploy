package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.entities.adapters.CsvReaderAdapter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LectorCsv implements CsvReaderAdapter {
  @Override
  public List<Object> readCsv(String path, String separator) {
    try (Reader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)) {
      CsvToBean<HechoCsv> csvToBean = new CsvToBeanBuilder<HechoCsv>(reader)
              .withType(HechoCsv.class)
              .withSeparator(separator.charAt(0))
              .withIgnoreLeadingWhiteSpace(true)
              .build();

      return new ArrayList<>(csvToBean.parse());
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }
}