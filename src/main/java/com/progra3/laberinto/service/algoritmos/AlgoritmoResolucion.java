package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import java.util.List;

public interface AlgoritmoResolucion {
    List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid);
    String getNombre();
    int getCeldasExploradas();
    long getTiempoEjecucion();
}
