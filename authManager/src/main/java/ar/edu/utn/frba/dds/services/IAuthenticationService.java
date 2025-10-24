package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.dto.LoginDto;
import ar.edu.utn.frba.dds.models.entities.dto.NewUserDto;
import ar.edu.utn.frba.dds.models.entities.dto.UserRolesAndAuthoritiesDto;
import ar.edu.utn.frba.dds.models.entities.dto.UserTokensDto;

public interface IAuthenticationService {
  UserTokensDto register(NewUserDto request);

  UserTokensDto login(LoginDto request);

  UserTokensDto refresh(String tokenHeader);

  UserRolesAndAuthoritiesDto getRolesAndAuthorities(String reqToken);
}
