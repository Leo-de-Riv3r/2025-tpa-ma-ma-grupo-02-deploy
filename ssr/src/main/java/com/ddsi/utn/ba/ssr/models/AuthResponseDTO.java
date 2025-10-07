package com.ddsi.utn.ba.ssr.models;

import lombok.Data;

@Data
public class AuthResponseDTO {
  private String accessToken;
  private String refreshToken;
}
