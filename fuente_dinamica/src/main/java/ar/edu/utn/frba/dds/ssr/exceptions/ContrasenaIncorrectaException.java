package ar.edu.utn.frba.dds.ssr.exceptions;

public class ContrasenaIncorrectaException extends RuntimeException {
    public ContrasenaIncorrectaException() {
        super("La contraseña no corresponde al email ingresado.");
    }
}