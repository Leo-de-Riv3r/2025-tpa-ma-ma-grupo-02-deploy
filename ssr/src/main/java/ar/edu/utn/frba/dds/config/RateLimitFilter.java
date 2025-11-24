package ar.edu.utn.frba.dds.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.github.bucket4j.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.classic(
            80,                           // ❗ límite de requests
            Refill.intervally(80, Duration.ofMinutes(1))
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

    String ip = request.getRemoteAddr();
    Bucket bucket = resolveBucket(ip);
//    String ip = request.getHeader("X-Forwarded-For");
//    if (ip == null) ip = request.getRemoteAddr();

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(429);
      response.setContentType("text/plain");
      response.getWriter().write("Demasiadas solicitudes. Intentá de nuevo en unos segundos.");
    }
  }
}
