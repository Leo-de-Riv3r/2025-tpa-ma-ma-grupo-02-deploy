package ar.edu.utn.frba.dds.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerController {
  //controller para busqueda de coleccion inexistente
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("Id de coleccion no encontrado");
  }
}
