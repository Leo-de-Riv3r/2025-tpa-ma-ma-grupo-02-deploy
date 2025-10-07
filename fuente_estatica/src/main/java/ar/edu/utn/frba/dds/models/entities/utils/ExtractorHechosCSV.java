package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExtractorHechosCSV {
  public List<Hecho> obtenerHechosCsv(String urlCsv, String separador) {
    try {
      URL url = new URL(urlCsv);
      List<Hecho> listaHechos = new ArrayList<>();
      HeaderColumnNameMappingStrategy<HechoCsv> strategy = new HeaderColumnNameMappingStrategy<>();
      strategy.setType(HechoCsv.class);

      CsvToBean<HechoCsv> csvToBean = new CsvToBeanBuilder<HechoCsv>(
          new InputStreamReader(url.openStream()))
          .withSeparator(',')
          .withMappingStrategy(strategy)
          .withIgnoreLeadingWhiteSpace(true)
          .build();

      List<HechoCsv> hechos = csvToBean.parse();

      hechos.forEach(hecho -> {
        LocalDateTime fechaHecho = hecho.getFecha().atStartOfDay();
        Hecho hechoDTO = new Hecho(hecho.getTitulo(), hecho.getDescripcion(), hecho.getCategoria(), hecho.getLatitud(), hecho.getLongitud(), fechaHecho, LocalDateTime.now());
        listaHechos.add(hechoDTO);
      });

      return listaHechos;
    } catch (Exception e) {
      System.out.println(e.getClass());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha encontrado un archivo csv valido");
    }
  }
}
