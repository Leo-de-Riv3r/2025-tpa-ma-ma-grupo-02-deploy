package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.exceptions.BlockedIpServerException;
import ar.edu.utn.frba.dds.models.BlockIpDto;
import ar.edu.utn.frba.dds.models.BlockedIpResDto;
import ar.edu.utn.frba.dds.models.PaginacionDtoBlockedIp;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class BlockedIpService {

  private final RestTemplate restTemplate;
  private final String externalServiceUrl;

  public BlockedIpService(RestTemplate restTemplate,
                          @Value("${external.ip-blocker.url}") String externalServiceUrl) {
    this.restTemplate = restTemplate;
    this.externalServiceUrl = externalServiceUrl;
  }

  public boolean isBlocked(String ip) {
    try {
      String url = UriComponentsBuilder.fromHttpUrl(externalServiceUrl + "/blocked")
          .queryParam("ip", ip)
          .toUriString();

      BlockedIpResDto blockedIpResDto = restTemplate.getForObject(url, BlockedIpResDto.class);

      return blockedIpResDto != null && blockedIpResDto.getBlocked();
    } catch (Exception e) {
      throw new BlockedIpServerException("Error al consultar lista de ips");
    }
  }

  public void blockIp(BlockIpDto blockIpDto){
    try {
    restTemplate.postForObject(externalServiceUrl + "/block", blockIpDto, String.class);
  } catch(Exception e)
  {
    throw new BlockedIpServerException("Error al intentar bloquear IP en servicio externo");
  }
}

  public void unblockIp(BlockIpDto blockIpDto) {
    try {
      restTemplate.postForObject(externalServiceUrl + "/unblock", blockIpDto, String.class);
    } catch (RestClientException e) {
      e.printStackTrace();
      throw new BlockedIpServerException("Error al intentar desbloquear IP en servicio externo");
    }
  }

  public PaginacionDtoBlockedIp getList(int page, int perPage) {
    try {
      String url = UriComponentsBuilder.fromHttpUrl(externalServiceUrl)
          .queryParam("page", page)
          .queryParam("perPage", perPage)
          .toUriString();

      return restTemplate.getForObject(url, PaginacionDtoBlockedIp.class);
    } catch (RestClientException e) {
      throw new BlockedIpServerException("Error al intentar obtener lista de ips");
    }
  }
}