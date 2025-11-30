package ar.edu.utn.frba.dds.config;

//import ar.edu.utn.frba.dds.services.BlockedIpService;
import ar.edu.utn.frba.dds.services.BlockedIpService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.spi.FileTypeDetector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IpBlockFilter implements Filter {
  private final BlockedIpService blockedIpService;

  public IpBlockFilter(BlockedIpService blockedIpService) {
    this.blockedIpService = blockedIpService;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    // 1. Obtener la IP Real (Considerando Proxies de Railway/Cloudflare)
    String clientIp = getClientIp(request);

    // 2. Verificar si está bloqueada
    if (blockedIpService.isBlocked(clientIp)) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.getWriter().write("ACCESO DENEGADO: Tu IP ha sido bloqueada manualmente.");
      return; // Cortamos la cadena aquí. No entra a la app.
    }

    chain.doFilter(req, res);
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      // El formato suele ser "IP_CLIENTE, PROXY1, PROXY2" -> Tomamos la primera
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
