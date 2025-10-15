package ar.edu.utn.frba.dds.ssr.models;

import java.util.List;
import lombok.Data;

@Data
public class RolesPermisosDTO {
  private String username;
  private Rol rol;
  private List<Permiso> permisos;
}
