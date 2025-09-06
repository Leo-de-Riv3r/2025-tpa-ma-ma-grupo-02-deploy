package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.DTO.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import jakarta.persistence.EntityNotFoundException;
import java.net.URL;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FuenteEstaticaService implements IFuenteEstaticaService {
  private IFuenteRepository fuenteRepository;

  public FuenteEstaticaService(IFuenteRepository fuenteRepository) {
    this.fuenteRepository = fuenteRepository;
  }

  @Override
  public FuenteCsvDTOOutput getFuente(Long id) {
    Fuente fuente = fuenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada"));
    return new FuenteCsvDTOOutput(fuente.getId(), fuente.getUrl(), fuente.getHechos().size());
  }

  @Override
  public List<HechoDTO> getHechos(Long id, Integer page, Integer per_page) {
    Fuente fuente = fuenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada"));
    List<HechoDTO> hechos = fuente.getHechos();
    if (per_page < 1) per_page = 1;
    if (per_page > 100) per_page = 100;
    if (page < 1) page = 1;
    Integer total = hechos.size();
    Integer inicio = (page - 1) * per_page;
    Integer fin = Math.min(inicio + per_page, total);
    List <HechoDTO> hechosPag = List.of();
    Integer lastPage = (int) Math.ceil((double) total / per_page);

    if (inicio > total) {
      System.out.println("0-9");
      hechosPag = hechos.subList(0, 9);
    } else {
      System.out.println("Inicio: " + inicio + ". Fin: " + fin);
      hechosPag = hechos.subList(inicio, fin);
    }
    return hechosPag;
  }

  public List<HechoDTO> extraeryGuardarHechos(String urlCsv, String separador) {
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

        return listaHechos;
      } catch (Exception e) {
        System.out.println(e.getClass());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha encontrado un archivo csv valido");
      }
  }

  @Override
  public FuenteCsvDTOOutput crearNuevaFuente(String link, String separador) {
    Fuente fuente = new Fuente();
    fuente.setHechos(extraeryGuardarHechos(link, separador));
    fuente.setUrl(link);
    Fuente fuenteCreada = fuenteRepository.save(fuente);
    return new FuenteCsvDTOOutput(fuenteCreada.getId(), link, fuenteCreada.getHechos().size());
  }

  @Override
  public void eliminarFuente(Long id) {
    fuenteRepository.deleteById(id);
  }
}

