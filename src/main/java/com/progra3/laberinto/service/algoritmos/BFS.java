package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BFS implements AlgoritmoResolucion {
    
    private int celdasExploradas;
    private long tiempoEjecucion;
    private final String nombre = "BFS";
    
    @Override
    public List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid) {
        long startTime = System.currentTimeMillis();
        celdasExploradas = 0;
        
        if (inicio == null || salida == null || grid == null) {
            return new ArrayList<>();
        }
        
        int filas = grid.size();
        int columnas = grid.get(0).size();
        
        // Estructuras para BFS
        Queue<Celda> cola = new LinkedList<>();
        Map<String, Celda> padres = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        
        cola.offer(inicio);
        visitados.add(inicio.getId());
        padres.put(inicio.getId(), null);
        //Esto seria para contar la celda inicial como explorada equivale a aristas no a nodos porque BFS explora por niveles
        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Derecha, Abajo, Izquierda, Arriba
        
        while (!cola.isEmpty()) {
            Celda actual = cola.poll();
            celdasExploradas++;
            
            // Si encontramos la salida
            if (actual.getX() == salida.getX() && actual.getY() == salida.getY()) {
                tiempoEjecucion = System.currentTimeMillis() - startTime;
                return reconstruirCamino(padres, salida);
            }
            
            // Explorar vecinos
            
            for (int[] dir : direcciones) {
                int nuevaX = actual.getX() + dir[0];
                int nuevaY = actual.getY() + dir[1];
                
                // Verificar límites del grid
                if (nuevaX >= 0 && nuevaX < columnas && nuevaY >= 0 && nuevaY < filas) {
                    Celda vecino = grid.get(nuevaY).get(nuevaX);
                    
                    // Si no es muro y no ha sido visitado
                    if (!vecino.getTipo().equals("MURO") && !visitados.contains(vecino.getId())) {
                        visitados.add(vecino.getId());
                        padres.put(vecino.getId(), actual);
                        cola.offer(vecino);
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
}