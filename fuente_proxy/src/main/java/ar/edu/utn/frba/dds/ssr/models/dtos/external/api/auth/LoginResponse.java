package ar.edu.utn.frba.dds.ssr.models.dtos.external.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
  private Boolean error;
  private String message;
  private LoginData data;

  public Boolean isError() {
    return error;
  }
}
