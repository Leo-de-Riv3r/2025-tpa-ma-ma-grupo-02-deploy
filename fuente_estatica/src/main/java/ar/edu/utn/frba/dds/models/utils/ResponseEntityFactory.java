package ar.edu.utn.frba.dds.models.utils;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFactory {
  public ResponseEntityFactory () {}

  public static ResponseEntity<Map<String, Object>> createOkEntity(String message) {
    Map<String, Object> messageBody = new HashMap<>();
    messageBody.put("mensaje", message);
    messageBody.put("timestamp", ZonedDateTime.now());
    return new ResponseEntity<>(messageBody, HttpStatus.OK);
  }

  public static ResponseEntity<Map<String, Object>> createNotFoundEntity(String message) {
    Map<String, Object> messageBody = new HashMap<>();
    messageBody.put("mensaje", message);
    messageBody.put("timestamp", ZonedDateTime.now());
    return new ResponseEntity<>(messageBody, HttpStatus.NOT_FOUND);
  }

  public static ResponseEntity<Map<String, Object>> createBadRequestEntity(String message) {
    Map<String, Object> messageBody = new HashMap<>();
    messageBody.put("mensaje", message);
    messageBody.put("timestamp", ZonedDateTime.now());
    return new ResponseEntity<>(messageBody, HttpStatus.BAD_REQUEST);
  }
}
