package ar.edu.utn.frba.dds.services.impl;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import ar.edu.utn.frba.dds.models.entities.utils.ExtractorHechosCSV;
import ar.edu.utn.frba.dds.models.repositories.IFuenteRepository;
import ar.edu.utn.frba.dds.services.IFuenteEstaticaService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
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
    if (per_page > 2000) per_page = 2000;
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

//  @Override
//  public FuenteCsvDTOOutput crearNuevaFuente(MultipartFile link) {
//    Fuente fuente = new Fuente();
//    fuente.setHechos(extractorHechosCSV.obtenerHechosCsv(link));
//    fuente.setUrl(link.getName());
//    Fuente fuenteCreada = fuenteRepository.save(fuente);
//    return new FuenteCsvDTOOutput(fuenteCreada.getId(), fuenteCreada.getUrl(), fuenteCreada.getHechos().size());
//  }

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

  @Override
  public Map<String, Object> validarCsv(MultipartFile file) {
    return extractorHechosCSV.obtenerDatosValidacion(file);
  }

  @Override
  public FuenteCsvDTOOutput crearNuevaFuente(MultipartFile link) throws IOException {
    Fuente fuente = new Fuente();
    List<Hecho> hechosCsv = this.extractorHechosCSV.obtenerHechos(link);
    fuente.setHechos(hechosCsv);
    fuente.setUrl(link.getOriginalFilename());
    Fuente fuenteGuardado = fuenteRepository.save(fuente);
    log.info("Nueva fuente csv de archivo {}", link.getOriginalFilename());
    return new FuenteCsvDTOOutput(fuente.getId(), link.getOriginalFilename(), fuenteGuardado.getHechos().size());
  }
}

