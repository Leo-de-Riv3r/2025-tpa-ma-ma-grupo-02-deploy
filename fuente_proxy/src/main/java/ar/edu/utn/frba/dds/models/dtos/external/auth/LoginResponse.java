package ar.edu.utn.frba.dds.models.dtos.external.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
  private boolean error;
  private String message;
  private LoginData data;
}
