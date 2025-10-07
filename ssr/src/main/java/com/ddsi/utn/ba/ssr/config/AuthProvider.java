package com.ddsi.utn.ba.ssr.config;

import com.ddsi.utn.ba.ssr.models.AuthResponseDTO;
import com.ddsi.utn.ba.ssr.models.RolesPermisosDTO;
import com.ddsi.utn.ba.ssr.services.MetamapaApiService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthProvider implements AuthenticationProvider {
  private final MetamapaApiService externalAuthService;

  public AuthProvider(MetamapaApiService externalAuthService) {
    this.externalAuthService = externalAuthService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    System.out.println(username);
    System.out.println(password);
    try {
      AuthResponseDTO authResponse = externalAuthService.login(username, password);

      if (authResponse == null) {
        throw new BadCredentialsException("Usuario o contraseña inválidos");
      }

      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      HttpServletRequest request = attributes.getRequest();

      request.getSession().setAttribute("accessToken", authResponse.getAccessToken());
      request.getSession().setAttribute("refreshToken", authResponse.getRefreshToken());
      request.getSession().setAttribute("username", username);

      RolesPermisosDTO rolesPermisos = externalAuthService.getRolesPermisos(authResponse.getAccessToken());

      request.getSession().setAttribute("rol", rolesPermisos.getRol());
      request.getSession().setAttribute("permisos", rolesPermisos.getPermisos());

      List<GrantedAuthority> authorities = new ArrayList<>();
      rolesPermisos.getPermisos().forEach(permiso -> {
        authorities.add(new SimpleGrantedAuthority(permiso.name()));
      });
      authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesPermisos.getRol().name()));

      return new UsernamePasswordAuthenticationToken(username, password, authorities);
    } catch (RuntimeException e) {
      throw new BadCredentialsException("Error en el sistema de autenticación: " + e.getMessage());
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
