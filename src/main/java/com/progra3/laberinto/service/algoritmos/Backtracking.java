package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Algoritmo de resolución de laberinto mediante Backtracking
 * con Ramificación y Poda.
 *
 * Explora todas las posibles rutas recursivamente, podando aquellas
 * que no pueden superar la mejor solución encontrada hasta el momento.
 */
@Component
public class Backtracking implements AlgoritmoResolucion {

    private int celdasExploradas;
    private long tiempoEjecucion;
    private final String nombre = "Backtracking con Ramificación y Poda";

    private int mejorDistancia;
    private List<Celda> mejorCamino;
    private int[][] direcciones = {
        {0, 1},   // Derecha
        {1, 0},   // Abajo
        {0, -1},  // Izquierda
        {-1, 0}   // Arriba
    };

    @Override
    public List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid) {
        long startTime = System.currentTimeMillis();
        celdasExploradas = 0;
        mejorDistancia = Integer.MAX_VALUE;
        mejorCamino = new ArrayList<>();

        if (inicio == null || salida == null || grid == null || grid.isEmpty()) {
            tiempoEjecucion = System.currentTimeMillis() - startTime;
            return new ArrayList<>();
        }

        Set<String> visitadas = new HashSet<>();
        List<Celda> caminoActual = new ArrayList<>();

        // Verificar que inicio y salida no sean muros
        if (inicio.getTipo().equals("MURO") || salida.getTipo().equals("MURO")) {
            tiempoEjecucion = System.currentTimeMillis() - startTime;
            return new ArrayList<>();
        }

        // Inicializar con la celda de inicio
        visitadas.add(inicio.getId());
        caminoActual.add(inicio);
        celdasExploradas++; // Contar la celda inicial

        backtrack(inicio, salida, grid, caminoActual, visitadas, 0);

        tiempoEjecucion = System.currentTimeMillis() - startTime;
        return new ArrayList<>(mejorCamino);
    }

    /**
     * Función recursiva principal del algoritmo de backtracking
     */
    private void backtrack(Celda actual, Celda salida, List<List<Celda>> grid,
                          List<Celda> caminoActual, Set<String> visitadas, int distanciaActual) {

        // Poda: si ya superó la mejor distancia encontrada
        if (distanciaActual >= mejorDistancia) {
            return;
        }

        // Si llegamos a la salida, actualizamos la mejor solución
        if (actual.getX() == salida.getX() && actual.getY() == salida.getY()) {
            if (distanciaActual < mejorDistancia) {
                mejorDistancia = distanciaActual;
                mejorCamino = new ArrayList<>(caminoActual);
            }
            return;
        }

        // Ramificación: explorar vecinos en todas las direcciones
        for (int[] dir : direcciones) {
            int nuevaX = actual.getX() + dir[0];
            int nuevaY = actual.getY() + dir[1];

            // Verificar límites del grid
            if (nuevaY >= 0 && nuevaY < grid.size() && 
                nuevaX >= 0 && nuevaX < grid.get(nuevaY).size()) {
                
                Celda vecino = grid.get(nuevaY).get(nuevaX);
                String vecinoId = vecino.getId();

                // Verificar si el vecino es transitable y no ha sido visitado
                if (!vecino.getTipo().equals("MURO") && !visitadas.contains(vecinoId)) {
                    
                    // Poda adicional con heurística (distancia Manhattan desde el VECINO)
                    int heuristica = Math.abs(vecino.getX() - salida.getX()) + 
                                   Math.abs(vecino.getY() - salida.getY());
                    
                    // Si la distancia actual + 1 (hacia el vecino) + heurística es mayor o igual a la mejor distancia, podar
                    if (distanciaActual + 1 + heuristica >= mejorDistancia) {
                        continue;
                    }

                    // Avanzar: marcar como visitada y agregar al camino
                    visitadas.add(vecinoId);
                    caminoActual.add(vecino);
                    celdasExploradas++;

                    // Llamada recursiva
                    backtrack(vecino, salida, grid, caminoActual, visitadas, distanciaActual + 1);

                    // Retroceder (Backtracking): deshacer los cambios
                    visitadas.remove(vecinoId);
                    caminoActual.remove(caminoActual.size() - 1);
                }
            }
        }
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int getCeldasExploradas() {
        return celdasExploradas;
    }

    @Override
    public long getTiempoEjecucion() {
        return tiempoEjecucion;
    }
}