package ar.edu.utn.frba.dds.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
  //controller para busqueda de coleccion inexistente
//  @ExceptionHandler(NullPointerException.class)
//  public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
//    return ResponseEntity
//        .status(HttpStatus.NOT_FOUND)
//        .body("Id de coleccion no encontrado");
//  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleNotFoundEntity(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
  //errores de consulta de hechos de fuentes
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
  }
}
