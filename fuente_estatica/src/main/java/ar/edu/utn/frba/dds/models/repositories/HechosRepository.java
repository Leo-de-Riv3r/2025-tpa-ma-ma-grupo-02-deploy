package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.DTO.HechoDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class HechosRepository implements IHechosRepository{
  private List<HechoDTO> hechos = new ArrayList<>();

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
  public List<HechoDTO> getHechos(Integer page, Integer perPage) {
    Integer total = hechos.size();
    Integer inicio = (page - 1) * perPage;
    Integer fin = Math.min(inicio + perPage, total);
    List <HechoDTO> hechosPag = List.of();
    Integer lastPage = (int) Math.ceil((double) total / perPage);

    if (inicio > total) {
      System.out.println("0-9");
      hechosPag = hechos.subList(0, 9);
    } else {
      System.out.println("Inicio: " + inicio + ". Fin: " + fin);
      hechosPag = hechos.subList(inicio, fin);
    }
//    @JsonProperty("current_page")
//    private Integer currentPage;
//    private List<HechoDTO> data;
//    //private String first_page_url;
//    //private Integer from;
//    @JsonProperty("last_page")
//    private Integer lastPage;
    return hechosPag;
  }
}
