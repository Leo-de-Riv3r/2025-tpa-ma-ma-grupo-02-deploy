package ar.edu.utn.frba.dds.models.dtos.output;

import ar.edu.utn.frba.dds.models.dtos.ColeccionDTOSalida;
import java.util.List;
import lombok.Data;

@Data
public class ColeccionDTOSalidaGQL {
  private String id;
  private String titulo;
  private String descripcion;
  private List<FuenteDTOOutput> fuentes;
  private Integer cantSolicitudesSpam;
  private List<CriterioDtoSalida> criterios = List.of();
  private String algoritmoConsenso;
  private PaginacionDto<HechoDtoSalida> hechos;
  public ColeccionDTOSalidaGQL(ColeccionDTOSalida dto){
    this.id = dto.getId();
    this.titulo = dto.getTitulo();
    this.descripcion = dto.getDescripcion();
    this.fuentes = dto.getFuentes();
    this.criterios = dto.getCriterios();
    this.algoritmoConsenso = dto.getAlgoritmoConsenso();
    this.cantSolicitudesSpam = dto.getCantSolicitudesSpam();
  }
}
