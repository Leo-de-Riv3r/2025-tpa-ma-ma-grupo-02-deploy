package ar.edu.utn.frba.dds.exceptions;

public class IpAlreadyBlockedException extends RuntimeException {
  public IpAlreadyBlockedException(String message) {
    super(message);
  }
}
