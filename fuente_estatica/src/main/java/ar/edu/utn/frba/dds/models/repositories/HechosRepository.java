package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import ar.edu.utn.frba.dds.models.DTO.HechosPagDTO;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HechosRepository implements IHechosRepository{
  private List<HechoDTO> hechos;

  @Override
  public HechoDTO save(HechoDTO hecho) {
    hechos.add(hecho);
    return hecho;
  }

  @Override
  public void setHechos (List<HechoDTO> hechos) {
    this.hechos = hechos;
  }

  @Override
  public HechosPagDTO getHechos(Integer page, Integer perPage) {
    Integer total = hechos.size();
    Integer inicio = (page - 1) * perPage;
    Integer fin = Math.min(inicio + perPage, total);
    List <HechoDTO> hechosPag = List.of();

    if (inicio > total) {
      hechosPag = hechos.subList(0, 9);
    }
//    @JsonProperty("current_page")
//    private Integer currentPage;
//    private List<HechoDTO> data;
//    //private String first_page_url;
//    //private Integer from;
//    @JsonProperty("last_page")
//    private Integer lastPage;
    return new HechosPagDTO(page, hechosPag);
  }
}
