package ar.edu.utn.frba.dds.Entities;

import ar.edu.utn.frba.dds.Enums.Estado;

public class Solicitud {
    private String titulo;
    private String texto;
    private Hecho hecho;
    private Estado estado;

    public Boolean estaFundado() {
        //TODO
        return texto.length() >= 500;
    }
}