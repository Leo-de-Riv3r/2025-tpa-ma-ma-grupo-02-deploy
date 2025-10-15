package ar.edu.utn.frba.dds.ssr.models.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExternalUser {
  private String username;
  private String password;
}