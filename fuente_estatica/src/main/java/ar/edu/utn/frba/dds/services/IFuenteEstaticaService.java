package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.DTO.FuenteCsvDTOOutput;
import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import java.util.List;

public interface IFuenteEstaticaService {

  FuenteCsvDTOOutput getFuente(Long id);

  List<HechoDTO> getHechos(Long id, Integer page, Integer per_page);

  FuenteCsvDTOOutput crearNuevaFuente(String link, String separador);

  void eliminarFuente(Long id);
}
