package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Greedy implements AlgoritmoResolucion {
    
    private int celdasExploradas;
    private long tiempoEjecucion;
    private final String nombre = "Greedy";
    
    @Override
    public List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid) {
        long startTime = System.currentTimeMillis();
        celdasExploradas = 0;
        
        if (inicio == null || salida == null || grid == null) {
            return new ArrayList<>();
        }
        
        int filas = grid.size();
        int columnas = grid.get(0).size();
        
        // Estructuras para Greedy
        PriorityQueue<CeldaHeuristica> colaPrioridad = new PriorityQueue<>();
        Map<String, Celda> padres = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        
        colaPrioridad.offer(new CeldaHeuristica(inicio, calcularHeuristica(inicio, salida)));
        visitados.add(inicio.getId());
        padres.put(inicio.getId(), null);
        
        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Derecha, Abajo, Izquierda, Arriba
        
        while (!colaPrioridad.isEmpty()) {
            CeldaHeuristica actual = colaPrioridad.poll();
            Celda celdaActual = actual.celda;
            celdasExploradas++;
            
            // Si encontramos la salida
            if (celdaActual.getX() == salida.getX() && celdaActual.getY() == salida.getY()) {
                tiempoEjecucion = System.currentTimeMillis() - startTime;
                return reconstruirCamino(padres, salida);
            }
            // Explorar vecinos
            for (int[] dir : direcciones) {
                int nuevaX = celdaActual.getX() + dir[0];
                int nuevaY = celdaActual.getY() + dir[1];
                
                // Verificar límites del grid
                if (nuevaX >= 0 && nuevaX < columnas && nuevaY >= 0 && nuevaY < filas) {
                    Celda vecino = grid.get(nuevaY).get(nuevaX);
                    
                    // Si no es muro y no ha sido visitado
                    if (!vecino.getTipo().equals("MURO") && !visitados.contains(vecino.getId())) {
                        visitados.add(vecino.getId());
                        padres.put(vecino.getId(), celdaActual);
                        
                        // Calcular heurística (distancia Manhattan a la salida)
                        double heuristica = calcularHeuristica(vecino, salida);
                        colaPrioridad.offer(new CeldaHeuristica(vecino, heuristica));
                    }
                }
            }
        }
        
        tiempoEjecucion = System.currentTimeMillis() - startTime;
        return new ArrayList<>(); // No se encontró camino
    }
    
    private double calcularHeuristica(Celda actual, Celda destino) {
        // Distancia Manhattan
        return Math.abs(actual.getX() - destino.getX()) + Math.abs(actual.getY() - destino.getY());
    }
    
    private List<Celda> reconstruirCamino(Map<String, Celda> padres, Celda salida) {
        List<Celda> camino = new ArrayList<>();
        Celda actual = salida;
        
        while (actual != null) {
            camino.add(actual);
            actual = padres.get(actual.getId());
        }
        
        Collections.reverse(camino);
        return camino;
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
    
    // Clase auxiliar para la cola de prioridad
    private static class CeldaHeuristica implements Comparable<CeldaHeuristica> {
        Celda celda;
        double heuristica;
        
        public CeldaHeuristica(Celda celda, double heuristica) {
            this.celda = celda;
            this.heuristica = heuristica;
        }
        
        @Override
        public int compareTo(CeldaHeuristica other) {
            return Double.compare(this.heuristica, other.heuristica);
        }
    }
}