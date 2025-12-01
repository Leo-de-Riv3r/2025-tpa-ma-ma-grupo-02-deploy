package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.services.BlockedIpService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.github.bucket4j.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ejecutar antes que Spring Security
public class RateLimitFilter extends OncePerRequestFilter {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
  private final BlockedIpService ipBlockService;

  // Inyectamos el servicio de bloqueo
  public RateLimitFilter(BlockedIpService ipBlockService) {

    this.ipBlockService = ipBlockService;
  }

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.classic(
            100, // 100 requests
            Refill.intervally(100, Duration.ofMinutes(1)) // por minuto
        ))
        .build();
  }

  private Bucket resolveBucket(String key) {
    return buckets.computeIfAbsent(key, k -> createNewBucket());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    // 1. Obtener la IP Real (CORREGIDO PARA RAILWAY)
    String clientIp = getClientIp(request);
    System.out.println("IP del cliente: " + clientIp);
    String path = request.getRequestURI();
    if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images") || path.startsWith("/favicon")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. CHEQUEO DE BLOQUEO MANUAL (Prioridad Alta)
//    if (ipBlockService.isBlocked(clientIp)) {
//      response.setStatus(403); // Forbidden
//      response.setContentType("text/plain");
//      response.getWriter().write("ACCESO DENEGADO: Tu IP ha sido bloqueada manualmente por un administrador.");
//      return; // Cortamos aquí
//    }

    // 3. CHEQUEO DE RATE LIMITING (Prioridad Media)
    Bucket bucket = resolveBucket(clientIp);

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(429); // Too Many Requests
      response.setContentType("text/plain");
      response.getWriter().write("Demasiadas solicitudes, intenta de nuevo en unos minutos.");
    }
  }

  // Método auxiliar para extraer la IP correctamente detrás de un Proxy
  private String getClientIp(HttpServletRequest request) {
//    String xForwardedFor = request.getHeader("X-Forwarded-For");
//    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
//      // En Railway/Cloudflare, el formato es "IP_CLIENTE, PROXY1, PROXY2"
//      // Tomamos la primera, que es la del cliente real.
//      return xForwardedFor.split(",")[0].trim();
//    }
//    return request.getRemoteAddr();
    System.out.println("header: " + request.getHeaderNames());
    String realIp = request.getHeader("Fly-Client-IP");
    if (realIp == null) {
      realIp = request.getHeader("X-Forwarded-For");
    }
    if (realIp == null) {
      realIp = request.getRemoteAddr();
    }

    return realIp;
  }
}