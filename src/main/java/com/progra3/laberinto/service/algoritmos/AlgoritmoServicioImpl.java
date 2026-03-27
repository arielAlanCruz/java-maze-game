package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlgoritmoServicioImpl implements AlgoritmoServicio {
    
    private final BFS bfs;
    private final DFS dfs;
    private final Dijkstra dijkstra;
    private final Greedy greedy;
    private final Backtracking backtracking;
    
    public AlgoritmoServicioImpl(BFS bfs, DFS dfs, Dijkstra dijkstra, Greedy greedy, Backtracking backtracking) {
        this.bfs = bfs;
        this.dfs = dfs;
        this.dijkstra = dijkstra;
        this.greedy = greedy;
        this.backtracking = backtracking;
    }
    
    // Getters para acceso desde LaberintoServicio
    public BFS getBfs() {
        return bfs;
    }
    
    public DFS getDfs() {
        return dfs;
    }
    
    public Dijkstra getDijkstra() {
        return dijkstra;
    }

    public Greedy getGreedy() {
        return greedy;
    }
    
    public Backtracking getBacktracking() {
    	return backtracking;
    }
    
    @Override
    public List<Celda> resolver(Celda inicio, Celda salida, List<List<Celda>> grid) {
        // Este método será llamado por los métodos específicos de cada algoritmo
        throw new UnsupportedOperationException("Usar métodos específicos: bfs(), dfs(), dijkstra()");
    }
    
    public List<Celda> bfs(Celda inicio, Celda salida, List<List<Celda>> grid) {
        return bfs.resolver(inicio, salida, grid);
    }
    
    public List<Celda> dfs(Celda inicio, Celda salida, List<List<Celda>> grid) {
        return dfs.resolver(inicio, salida, grid);
    }
    
    public List<Celda> dijkstra(Celda inicio, Celda salida, List<List<Celda>> grid) {
        return dijkstra.resolver(inicio, salida, grid);
    }

    public List<Celda> greedy(Celda inicio, Celda salida, List<List<Celda>> grid) {
        return greedy.resolver(inicio, salida, grid);
    }
    
    public List<Celda> backtracking(Celda inicio, Celda salida, List<List<Celda>> grid) {
        return backtracking.resolver(inicio, salida, grid);
    }
    
    @Override
    public String getNombre() {
        return "AlgoritmoServicio";
    }
    
    @Override
    public int getCeldasExploradas() {
        return 0; // Se obtiene del algoritmo específico usado
    }
    
    @Override
    public long getTiempoEjecucion() {
        return 0; // Se obtiene del algoritmo específico usado
    }
    
}
