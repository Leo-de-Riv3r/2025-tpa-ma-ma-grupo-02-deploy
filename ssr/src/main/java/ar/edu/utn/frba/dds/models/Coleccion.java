package ar.edu.utn.frba.dds.models;

import java.util.List;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Coleccion {
  private String id;
  private String titulo;
  private String descripcion;
  private List<Fuente> fuentes;
  private int cantSolicitudesSpam;
  private List<CriterioDtoEntrada> criterios;
  private String algoritmoConsenso;
}

