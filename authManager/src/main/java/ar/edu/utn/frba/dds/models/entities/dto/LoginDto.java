package ar.edu.utn.frba.dds.models.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginDto {
  private String username;
  private String password;
}
