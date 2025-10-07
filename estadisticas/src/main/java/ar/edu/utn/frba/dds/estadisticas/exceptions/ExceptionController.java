package ar.edu.utn.frba.dds.estadisticas.exceptions;

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
}
