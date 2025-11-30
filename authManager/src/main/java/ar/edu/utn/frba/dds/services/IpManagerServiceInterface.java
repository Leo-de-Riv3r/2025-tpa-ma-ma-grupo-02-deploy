package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.dto.BlockedIpDto;
import ar.edu.utn.frba.dds.models.entities.dto.PaginacionDto;

public interface IpManagerServiceInterface {
  PaginacionDto<BlockedIpDto> getIpList(int page, int perPage);
}
