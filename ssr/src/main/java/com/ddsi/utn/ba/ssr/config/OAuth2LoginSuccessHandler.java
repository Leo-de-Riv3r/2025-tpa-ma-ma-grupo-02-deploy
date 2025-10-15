package com.ddsi.utn.ba.ssr.config;

import com.ddsi.utn.ba.ssr.exceptions.ExternalApiException;
import com.ddsi.utn.ba.ssr.models.AuthResponseDTO;
import com.ddsi.utn.ba.ssr.models.utils.ExternalUser;
import com.ddsi.utn.ba.ssr.models.utils.UserConverter;
import com.ddsi.utn.ba.ssr.services.MetamapaApiService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final MetamapaApiService metamapaApiService;
  private final UserConverter userConverter;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

    // Extraer email o nombre
    System.out.println("Objeto user: " + oauthUser);
    System.out.println("Enlace:" + request.getRequestURL());
    String url = request.getRequestURL().toString();


    ExternalUser externalUser = userConverter.getUser(oauthUser, url);
    String username = externalUser.getUsername();
    String password = externalUser.getPassword();
    try {
      // Intentar loguearse en el servicio externo
      AuthResponseDTO tokens = metamapaApiService.login(username, password);

      // Si no existe, lo registramos y volvemos a loguear
      if (tokens == null) {
        System.out.println("Usuario no encontrado, registrando...");
        boolean registered = metamapaApiService.register(username, password);
        if (!registered) {
          throw new RuntimeException("No se pudo registrar el usuario externo");
        }
        tokens = metamapaApiService.login(username, password);
      }

      // Guardar tokens en la sesión
      request.getSession().setAttribute("accessToken", tokens.getAccessToken());
      request.getSession().setAttribute("refreshToken", tokens.getRefreshToken());
      request.getSession().setAttribute("username", username);
      // Obtener roles y permisos
      var rolesPermisos = metamapaApiService.getRolesPermisos(tokens.getAccessToken());
      request.getSession().setAttribute("rol", rolesPermisos.getRol());
      request.getSession().setAttribute("permisos", rolesPermisos.getPermisos());

      List<GrantedAuthority> authorities = new ArrayList<>();
      rolesPermisos.getPermisos().forEach(permiso -> {
        authorities.add(new SimpleGrantedAuthority(permiso.name()));
      });
      authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesPermisos.getRol().name()));
      // Redirigir al home
      response.sendRedirect("/colecciones");
    }
    catch( HttpClientErrorException e) {
      if (e.getStatusCode().value() == 409) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.sendRedirect("/registro?userExists");
      }
    }
    catch (ExternalApiException e) {
      response.sendRedirect("/login?authErr");
      SecurityContextHolder.clearContext();
      request.getSession().invalidate();
    }

    catch (Exception e) {
      System.out.println(e.getClass());
      response.sendRedirect("/login?auth0err");
      SecurityContextHolder.clearContext();
      request.getSession().invalidate();
    }
  }
}