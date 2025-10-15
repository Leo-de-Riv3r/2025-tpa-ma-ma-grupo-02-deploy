package ar.edu.utn.frba.dds.ssr.estadisticas.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleNotFoundEntity(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  //no pudo conectar a coleccion
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handlerNotFoundColeccion(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<String> handlerNotFoundColeccion(EntityExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

}
