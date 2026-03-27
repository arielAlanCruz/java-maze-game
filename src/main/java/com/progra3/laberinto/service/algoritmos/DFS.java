package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DFS implements AlgoritmoResolucion {
    
    private int celdasExploradas;
    private long tiempoEjecucion;
    private final String nombre = "DFS";
    
    @Override
    public List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid) {
        long startTime = System.currentTimeMillis();
        celdasExploradas = 0;
        
        if (inicio == null || salida == null || grid == null) {
            return new ArrayList<>();
        }
        
        int filas = grid.size();
        int columnas = grid.get(0).size();
        
        // Estructuras para DFS
        Stack<Celda> pila = new Stack<>();
        Map<String, Celda> padres = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        
        pila.push(inicio);
        visitados.add(inicio.getId());
        padres.put(inicio.getId(), null);
        
        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Derecha, Abajo, Izquierda, Arriba
        
        while (!pila.isEmpty()) {
            Celda actual = pila.pop();
            celdasExploradas++;
            
            // Si encontramos la salida
            if (actual.getX() == salida.getX() && actual.getY() == salida.getY()) {
                tiempoEjecucion = System.currentTimeMillis() - startTime;
                return reconstruirCamino(padres, salida);
            }
            
            // Explorar vecinos (en orden inverso para mantener el orden natural)
            for (int i = direcciones.length - 1; i >= 0; i--) {
                int[] dir = direcciones[i];
                int nuevaX = actual.getX() + dir[0];
                int nuevaY = actual.getY() + dir[1];
                
                // Verificar límites del grid
                if (nuevaX >= 0 && nuevaX < columnas && nuevaY >= 0 && nuevaY < filas) {
                    Celda vecino = grid.get(nuevaY).get(nuevaX);
                    
                    // Si no es muro y no ha sido visitado
                    if (!vecino.getTipo().equals("MURO") && !visitados.contains(vecino.getId())) {
                        visitados.add(vecino.getId());
                        padres.put(vecino.getId(), actual);
                        pila.push(vecino);
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
