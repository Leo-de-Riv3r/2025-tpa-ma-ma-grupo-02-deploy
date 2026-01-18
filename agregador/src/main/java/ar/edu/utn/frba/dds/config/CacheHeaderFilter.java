package ar.edu.utn.frba.dds.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class CacheHeaderFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    // Ponemos el header por defecto; si quieres lógica compleja puedes usar interceptores
    httpServletResponse.addHeader("X-Cache-Provider", "Redis-Spring-Cache");

    chain.doFilter(request, response);
  }
}
