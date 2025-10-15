package ar.edu.utn.frba.dds.ssr.models;

import lombok.Data;

@Data
public class AuthResponseDTO {
  private String accessToken;
  private String refreshToken;
}
