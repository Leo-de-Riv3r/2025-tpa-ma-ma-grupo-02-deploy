package ar.edu.utn.frba.dds.models;

import lombok.Data;

@Data
public class BlockedIpResDto {
  private String ip;
  private Boolean blocked;
}
