package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.dto.BlockIpDto;
import ar.edu.utn.frba.dds.models.entities.dto.BlockedIpDto;
import ar.edu.utn.frba.dds.models.entities.dto.BlockedIpResDto;
import ar.edu.utn.frba.dds.models.entities.dto.PaginacionDto;
import ar.edu.utn.frba.dds.services.IpManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/access")
public class IpManagerController {
  private final IpManagerService ipManagerService;

  public IpManagerController(IpManagerService ipManagerService) {
    this.ipManagerService = ipManagerService;
  }

  @PostMapping("/block")
  public ResponseEntity<String> blockNewIp(
      @RequestBody BlockIpDto blockIpDto
      ) {
    ipManagerService.blockNewIp(blockIpDto.getIp(), blockIpDto.getMotivo());
    return ResponseEntity.ok("IP bloqueada");
  }

  @PostMapping("/unblock")
  public ResponseEntity<String> unblockIp(
      @RequestBody BlockIpDto blockIpDto
  ) {
    ipManagerService.unblock(blockIpDto.getIp(), blockIpDto.getMotivo());
    return ResponseEntity.ok("IP desbloqueada");
  }

  @GetMapping("/blocked")
  public ResponseEntity<BlockedIpResDto> isIpBlocked (
      @RequestParam(required = true) String ip
  ) {
    Boolean blocked = ipManagerService.isIpBlocked(ip);
    return ResponseEntity.ok(new BlockedIpResDto(ip, blocked));
  }

  @GetMapping("/")
  public ResponseEntity<PaginacionDto<BlockedIpDto>> getIpList (
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "10") int perPage
  ) {
    return ResponseEntity.ok(ipManagerService.getIpList(page, perPage));
  }
}
