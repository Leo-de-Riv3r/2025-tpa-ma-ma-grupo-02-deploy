package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import ar.edu.utn.frba.dds.models.DTO.HechosPagDTO;
import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.repositories.HechosRepository;
import ar.edu.utn.frba.dds.models.repositories.IHechosRepository;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.net.URL;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuenteEstaticaService implements IFuenteEstaticaService {
  private IHechosRepository hechosRepository;

  public FuenteEstaticaService(IHechosRepository hechosRepository) {
    this.hechosRepository = hechosRepository;
  }
  public HechosPagDTO getHechos(Integer page, Integer per_page) {
    if (per_page < 1) per_page = 1;
    if (per_page > 100) per_page = 100;
    return hechosRepository.getHechos(page, per_page);
  }

  public void extraeryGuardarHechos(String urlCsv, String separador) {
      try {
        URL url = new URL(urlCsv);
        List<HechoDTO> listaHechos = new ArrayList<>();
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
          HechoDTO hechoDTO = new HechoDTO(hecho.getTitulo(), hecho.getDescripcion(), hecho.getCategoria(), hecho.getLatitud(), hecho.getLongitud(), fechaHecho, LocalDateTime.now());
          listaHechos.add(hechoDTO);
        });

        hechosRepository.setHechos(listaHechos);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
}

