package ar.edu.utn.frba.dds.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

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

  @ExceptionHandler(RuntimeException.class)
  public String handleRuntimeException(RuntimeException ex) {
    return "notserver.html";
  }

  @ExceptionHandler(ResourceAccessException.class)
  public String handleRuntimeException(ResourceAccessException ex) {
    return "notserver.html";
  }

  @ExceptionHandler(Exception.class)
  public String handleError(HttpServletRequest req, Exception ex, Model model) {
    model.addAttribute("titulo", ex.getClass());
    model.addAttribute("descripcion", ex.getMessage());
    return "error";
  }

//  @ExceptionHandler(value = Exception.class)
//  public ModelAndView
//  defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
//    // If the exception is annotated with @ResponseStatus rethrow it and let
//    // the framework handle it - like the OrderNotFoundException example
//    // at the start of this post.
//    // AnnotationUtils is a Spring Framework utility class.
//    if (AnnotationUtils.findAnnotation
//        (e.getClass(), ResponseStatus.class) != null)
//      throw e;
//
//    // Otherwise setup and send the user to a default error-view.
//    ModelAndView mav = new ModelAndView();
//    mav.addObject("exception", e);
//    mav.addObject("url", req.getRequestURL());
//    mav.setViewName(DEFAULT_ERROR_VIEW);
//    return mav;
//  }
}
