package com.progra3.laberinto.dto;

public class AlgoritmoResultadoDto {
    private String algoritmo;
    private int largoCamino;
    private int celdasExploradas;
    private long tiempoEjecucionMs;
    private boolean exito;

    public AlgoritmoResultadoDto() {}

    public AlgoritmoResultadoDto(String algoritmo, int largoCamino, int celdasExploradas, long tiempoEjecucionMs, boolean exito) {
        this.algoritmo = algoritmo;
        this.largoCamino = largoCamino;
        this.celdasExploradas = celdasExploradas;
        this.tiempoEjecucionMs = tiempoEjecucionMs;
        this.exito = exito;
    }

    public String getAlgoritmo() { return algoritmo; }
    public void setAlgoritmo(String algoritmo) { this.algoritmo = algoritmo; }

    public int getLargoCamino() { return largoCamino; }
    public void setLargoCamino(int largoCamino) { this.largoCamino = largoCamino; }

    public int getCeldasExploradas() { return celdasExploradas; }
    public void setCeldasExploradas(int celdasExploradas) { this.celdasExploradas = celdasExploradas; }

    public long getTiempoEjecucionMs() { return tiempoEjecucionMs; }
    public void setTiempoEjecucionMs(long tiempoEjecucionMs) { this.tiempoEjecucionMs = tiempoEjecucionMs; }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
}
