package ar.edu.utn.frba.dds.models.entities.dto;

import lombok.Data;

@Data
public class BlockedIpResDto {
  private String ip;
  private Boolean blocked;

  public BlockedIpResDto(String ip, Boolean blocked) {
    this.ip = ip;
    this.blocked = blocked;
  }

}
