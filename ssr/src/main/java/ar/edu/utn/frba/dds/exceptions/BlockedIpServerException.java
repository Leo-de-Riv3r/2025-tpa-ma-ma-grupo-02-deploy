package ar.edu.utn.frba.dds.exceptions;

public class BlockedIpServerException extends RuntimeException {
  public BlockedIpServerException(String message) {
    super(message);
  }
}
