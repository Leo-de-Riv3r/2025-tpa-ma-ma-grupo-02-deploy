package ar.edu.utn.frba.dds.exceptions;

import ar.edu.utn.frba.dds.ExternalApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(AuthorizationDeniedException.class)
  public String handleAuthorizationDeniedException(HttpServletRequest req, AuthorizationDeniedException ex) {
    return "403";
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public String handleNotFound(HttpClientErrorException ex) {
    return "404";
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public String handleNoResourceFound(NoResourceFoundException ex) {
    return "404";
  }

  @ExceptionHandler(RuntimeException.class)
  public String handleRuntimeException(RuntimeException ex) {
    return "notserver.html";
  }

  @ExceptionHandler(ResourceAccessException.class)
  public String handleRuntimeException(ResourceAccessException ex) {
    return "notserver.html";
  }

  @ExceptionHandler(ExternalApiException.class)
  public String handleExternalApiException(ExternalApiException e) {
    return "externalApiError.html";
  }

  @ExceptionHandler(Exception.class)
  public String handleError(HttpServletRequest req, Exception ex, Model model) {
    model.addAttribute("titulo", ex.getClass());
    model.addAttribute("descripcion", ex.getMessage());
    return "error";
  }

}
