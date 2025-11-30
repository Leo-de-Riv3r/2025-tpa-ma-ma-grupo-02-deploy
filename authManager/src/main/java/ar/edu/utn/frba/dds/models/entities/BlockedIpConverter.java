package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.dto.BlockedIpDto;

public class BlockedIpConverter {
  public static BlockedIpDto fromEntity(BlockedIp blockedIp) {
    BlockedIpDto dto = new BlockedIpDto();
    dto.setIp(blockedIp.getIp());
    dto.setMotivo(blockedIp.getMotivo());
    dto.setEstado(blockedIp.getEstado());
    dto.setFechaModificacion(blockedIp.getFechaModificacion());
    return dto;
  }

}
