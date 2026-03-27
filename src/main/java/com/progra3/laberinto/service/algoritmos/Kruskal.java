package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Kruskal implements AlgoritmoGeneracion {
    
    private final String nombre = "Kruskal";
    
    @Override
    public List<List<Celda>> generarLaberinto(int ancho, int alto, String laberintoId) {
        // Crear grid inicial con todas las celdas como muros
        List<List<Celda>> grid = new ArrayList<>();
        for (int y = 0; y < alto; y++) {
            List<Celda> fila = new ArrayList<>();
            for (int x = 0; x < ancho; x++) {
                String celdaId = laberintoId + "-" + x + "-" + y;
                Celda celda = new Celda(celdaId, x, y, "MURO", laberintoId);
                fila.add(celda);
            }
            grid.add(fila);
        }
        
        // Algoritmo de Kruskal para generar laberinto
        List<Arista> aristas = generarAristas(grid, ancho, alto);
        Collections.shuffle(aristas); // Orden aleatorio para Kruskal
        
        UnionFind unionFind = new UnionFind(ancho * alto);
        
        // Convertir coordenadas a índices únicos
        Map<String, Integer> coordenadasAIndice = new HashMap<>();
        int indice = 0;
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                coordenadasAIndice.put(x + "," + y, indice++);
            }
        }
        
        // Procesar aristas en orden aleatorio
        for (Arista arista : aristas) {
            int indice1 = coordenadasAIndice.get(arista.x1 + "," + arista.y1);
            int indice2 = coordenadasAIndice.get(arista.x2 + "," + arista.y2);
            
            if (!unionFind.connected(indice1, indice2)) {
                unionFind.union(indice1, indice2);
                
                // Marcar celdas como libres
                grid.get(arista.y1).get(arista.x1).setTipo("LIBRE");
                grid.get(arista.y2).get(arista.x2).setTipo("LIBRE");
                
                // Marcar pasillo entre ellas como libre
                int pasoX = (arista.x1 + arista.x2) / 2;
                int pasoY = (arista.y1 + arista.y2) / 2;
                grid.get(pasoY).get(pasoX).setTipo("LIBRE");
            }
        }
        
        // Establecer celda de inicio y salida
        establecerInicioYSalida(grid, ancho, alto, laberintoId);
        
        return grid;
    }
    
    private List<Arista> generarAristas(List<List<Celda>> grid, int ancho, int alto) {
        List<Arista> aristas = new ArrayList<>();
        
        // Generar aristas entre celdas impares (manteniendo conectividad)
        for (int y = 1; y < alto; y += 2) {
            for (int x = 1; x < ancho; x += 2) {
                // Arista hacia la derecha
                if (x + 2 < ancho) {
                    aristas.add(new Arista(x, y, x + 2, y));
                }
                // Arista hacia abajo
                if (y + 2 < alto) {
                    aristas.add(new Arista(x, y, x, y + 2));
                }
            }
        }
        
        return aristas;
    }
    
    private void establecerInicioYSalida(List<List<Celda>> grid, int ancho, int alto, String laberintoId) {
        Random random = new Random();
        
        // Buscar celdas libres en los bordes para inicio y salida
        List<Celda> celdasBorde = new ArrayList<>();
        
        // Bordes superior e inferior
        for (int x = 0; x < ancho; x++) {
            if (grid.get(0).get(x).getTipo().equals("LIBRE")) {
                celdasBorde.add(grid.get(0).get(x));
            }
            if (grid.get(alto - 1).get(x).getTipo().equals("LIBRE")) {
                celdasBorde.add(grid.get(alto - 1).get(x));
            }
        }
        
        // Bordes izquierdo y derecho
        for (int y = 0; y < alto; y++) {
            if (grid.get(y).get(0).getTipo().equals("LIBRE")) {
                celdasBorde.add(grid.get(y).get(0));
            }
            if (grid.get(y).get(ancho - 1).getTipo().equals("LIBRE")) {
                celdasBorde.add(grid.get(y).get(ancho - 1));
            }
        }
        
        // Si no hay suficientes celdas libres en los bordes, crear pasillos hacia los bordes
        if (celdasBorde.size() < 2) {
            System.out.println("Creando pasillos hacia los bordes para inicio y salida...");
            
            // Buscar celdas libres en el interior
            List<Celda> celdasLibres = new ArrayList<>();
            for (int y = 1; y < alto - 1; y++) {
                for (int x = 1; x < ancho - 1; x++) {
                    if (grid.get(y).get(x).getTipo().equals("LIBRE")) {
                        celdasLibres.add(grid.get(y).get(x));
                    }
                }
            }
            
            if (!celdasLibres.isEmpty()) {
                // Crear pasillo hacia el borde superior
                Celda celdaInterior = celdasLibres.get(random.nextInt(celdasLibres.size()));
                int x = celdaInterior.getX();
                int y = celdaInterior.getY();
                
                // Crear pasillo hacia arriba
                while (y > 0) {
                    y--;
                    grid.get(y).get(x).setTipo("LIBRE");
                }
                celdasBorde.add(grid.get(0).get(x));
                
                // Crear pasillo hacia el borde inferior
                celdaInterior = celdasLibres.get(random.nextInt(celdasLibres.size()));
                x = celdaInterior.getX();
                y = celdaInterior.getY();
                
                // Crear pasillo hacia abajo
                while (y < alto - 1) {
                    y++;
                    grid.get(y).get(x).setTipo("LIBRE");
                }
                celdasBorde.add(grid.get(alto - 1).get(x));
            }
        }
        
        // Establecer inicio y salida
        if (celdasBorde.size() >= 2) {
            // Establecer inicio
            Celda inicio = celdasBorde.get(random.nextInt(celdasBorde.size()));
            inicio.setTipo("INICIO");
            System.out.println("Celda inicio establecida en: (" + inicio.getX() + ", " + inicio.getY() + ")");
            
            // Establecer salida (diferente al inicio)
            List<Celda> celdasSalida = new ArrayList<>(celdasBorde);
            celdasSalida.remove(inicio);
            if (!celdasSalida.isEmpty()) {
                Celda salida = celdasSalida.get(random.nextInt(celdasSalida.size()));
                salida.setTipo("SALIDA");
                System.out.println("Celda salida establecida en: (" + salida.getX() + ", " + salida.getY() + ")");
            }
        } else {
            System.out.println("ERROR: No se pudieron establecer celdas de inicio y salida");
        }
    }
    
    @Override
    public String getNombre() {
        return nombre;
    }
    
    // Clase auxiliar para representar aristas
    private static class Arista {
        int x1, y1, x2, y2;
        
        public Arista(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
    
    // Implementación simple de Union-Find para Kruskal
    private static class UnionFind {
        private int[] parent;
        private int[] rank;
        
        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }
        
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // estructura de compresión de ruta
                
            }
            return parent[x];
        }
        
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
        
        public boolean connected(int x, int y) {
            return find(x) == find(y);
        }
    }
}
