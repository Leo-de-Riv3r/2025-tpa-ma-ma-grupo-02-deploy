package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.HechoCsv;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.utils.ExtractorHechosCSV;
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
  private ExtractorHechosCSV extractorHechosCSV;
  public FuenteEstaticaService(IFuenteRepository fuenteRepository, ExtractorHechosCSV extractorHechosCSV) {
    this.fuenteRepository = fuenteRepository;
    this.extractorHechosCSV = extractorHechosCSV;
  }

  @Override
  public FuenteCsvDTOOutput getFuente(Long id) {
    Fuente fuente = fuenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada"));
    return new FuenteCsvDTOOutput(fuente.getId(), fuente.getUrl(), fuente.getHechos().size());
  }

  @Override
  public List<Hecho> getHechos(Long id, Integer page, Integer per_page) {
    Fuente fuente = fuenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Fuente no encontrada"));
    List<Hecho> hechos = fuente.getHechos();
    if (page < 1) page = 1;
    if (per_page > 100) per_page = 100;
    if (page < 1) page = 1;
    Integer total = hechos.size();
    Integer inicio = (page - 1) * per_page;
    Integer fin = Math.min(inicio + per_page, total);
    List <Hecho> hechosPag = List.of();
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

  @Override
  public FuenteCsvDTOOutput crearNuevaFuente(String link, String separador) {
    if ( link == null || separador == null || link.isBlank() || separador.isBlank()) {
      throw new IllegalArgumentException("link de archivo .csv o separador no pueden ser nulos");
    }
    Fuente fuente = new Fuente();
    fuente.setHechos(extractorHechosCSV.obtenerHechosCsv(link, separador));
    fuente.setUrl(link);
    Fuente fuenteCreada = fuenteRepository.save(fuente);
    return new FuenteCsvDTOOutput(fuenteCreada.getId(), link, fuenteCreada.getHechos().size());
  }

  @Override
  public void eliminarFuente(Long id) {
    fuenteRepository.deleteById(id);
  }

  @Override
  public List<FuenteCsvDTOOutput> obtenerFuentesDTO() {
    List<Fuente> fuentes = this.getFuentes();
    return fuentes.stream().map(f -> new FuenteCsvDTOOutput(f.getId(), f.getUrl(), f.getHechos().size())).toList();
  }

  @Override
  public List<Fuente> getFuentes() {
    return fuenteRepository.findAll();
  }
}

