package ar.edu.utn.frba.dds.ssr.models;

import lombok.Data;

@Data
public class Fuente {
  private String id;
  private String tipoFuente;
  private String url;
  private int cantidadHechos;
}

