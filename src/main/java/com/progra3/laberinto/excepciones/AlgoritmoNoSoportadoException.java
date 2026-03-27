package com.progra3.laberinto.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlgoritmoNoSoportadoException extends RuntimeException {
    
    public AlgoritmoNoSoportadoException(String algoritmo) {
        super("Algoritmo no soportado: " + algoritmo);
    }
    
    public AlgoritmoNoSoportadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
