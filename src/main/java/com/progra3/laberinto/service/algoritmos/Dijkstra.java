package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Dijkstra implements AlgoritmoResolucion {
    
    private int celdasExploradas;
    private long tiempoEjecucion;
    private final String nombre = "Dijkstra";
    
    @Override
    public List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid) {
        long startTime = System.currentTimeMillis();
        celdasExploradas = 0;
        
        if (inicio == null || salida == null || grid == null) {
            return new ArrayList<>();
        }
        
        int filas = grid.size();
        int columnas = grid.get(0).size();
        
        // Estructuras para Dijkstra
        Map<String, Integer> distancias = new HashMap<>();
        Map<String, Celda> padres = new HashMap<>();
        PriorityQueue<CeldaDistancia> colaPrioridad = new PriorityQueue<>();
        Set<String> visitados = new HashSet<>();
        
        // Inicializar distancias
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                Celda celda = grid.get(y).get(x);
                distancias.put(celda.getId(), Integer.MAX_VALUE);
            }
        }
        
        distancias.put(inicio.getId(), 0);
        colaPrioridad.offer(new CeldaDistancia(inicio, 0));
        
        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Derecha, Abajo, Izquierda, Arriba
        
        while (!colaPrioridad.isEmpty()) {
            CeldaDistancia actual = colaPrioridad.poll();
            Celda celdaActual = actual.celda;
            
            if (visitados.contains(celdaActual.getId())) {
                continue;
            }
            
            visitados.add(celdaActual.getId());
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
                        int nuevaDistancia = distancias.get(celdaActual.getId()) + 1; // Peso uniforme de 1
                        
                        if (nuevaDistancia < distancias.get(vecino.getId())) {
                            distancias.put(vecino.getId(), nuevaDistancia);
                            padres.put(vecino.getId(), celdaActual);
                            colaPrioridad.offer(new CeldaDistancia(vecino, nuevaDistancia));
                        }
                    }
                }
            }
        }
        
        tiempoEjecucion = System.currentTimeMillis() - startTime;
        return new ArrayList<>(); // No se encontró camino
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
    private static class CeldaDistancia implements Comparable<CeldaDistancia> {
        Celda celda;
        int distancia;
        
        public CeldaDistancia(Celda celda, int distancia) {
            this.celda = celda;
            this.distancia = distancia;
        }
        
        @Override
        public int compareTo(CeldaDistancia other) {
            return Integer.compare(this.distancia, other.distancia);
        }
    }
}
