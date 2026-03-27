package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import java.util.List;

public interface AlgoritmoGeneracion {
    List<List<Celda>> generarLaberinto(int ancho, int alto, String laberintoId);
    String getNombre();
}
