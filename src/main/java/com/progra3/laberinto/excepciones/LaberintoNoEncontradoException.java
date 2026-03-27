package com.progra3.laberinto.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LaberintoNoEncontradoException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LaberintoNoEncontradoException(String laberintoId) {
        super("Laberinto no encontrado con ID: " + laberintoId);
    }
    
    public LaberintoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
