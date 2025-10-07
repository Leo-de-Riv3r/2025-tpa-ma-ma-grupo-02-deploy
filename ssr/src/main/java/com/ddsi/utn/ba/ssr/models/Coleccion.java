package com.ddsi.utn.ba.ssr.models;

import java.util.List;
import lombok.Data;

@Data
public class Coleccion {
  private String id;
  private String titulo;
  private String descripcion;
  private List<Fuente> fuentes;
  private int cantSolicitudesSpam;
  private List<String> criterios;
  private String algoritmoConsenso;
}

