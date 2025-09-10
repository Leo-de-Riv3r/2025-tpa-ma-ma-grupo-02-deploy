package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.DTO.output.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.Fuente;
import java.util.List;

public interface IFuenteEstaticaService {

  FuenteCsvDTOOutput getFuente(Long id);

  List<Hecho> getHechos(Long id, Integer page, Integer per_page);

  FuenteCsvDTOOutput crearNuevaFuente(String link, String separador);

  void eliminarFuente(Long id);

  List<FuenteCsvDTOOutput> obtenerFuentesDTO();

  List<Fuente> getFuentes();
}
