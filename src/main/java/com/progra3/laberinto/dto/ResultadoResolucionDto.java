package com.progra3.laberinto.dto;

import com.progra3.laberinto.model.Celda;
import java.util.List;

public class ResultadoResolucionDto {
	private List<Celda> camino;
	private int celdasExploradas;
	private long tiempoEjecucion;
	private String algoritmo;
	private boolean exito;

	// Constructores, Getters y Setters
	public ResultadoResolucionDto() {
	}

	public ResultadoResolucionDto(List<Celda> camino, int celdasExploradas, long tiempoEjecucion, String algoritmo,
			boolean exito) {
		this.camino = camino;
		this.celdasExploradas = celdasExploradas;
		this.tiempoEjecucion = tiempoEjecucion;
		this.algoritmo = algoritmo;
		this.exito = exito;
	}

	// Getters y Setters...
	public List<Celda> getCamino() {
		return camino;
	}

	public void setCamino(List<Celda> camino) {
		this.camino = camino;
	}

	public int getCeldasExploradas() {
		return celdasExploradas;
	}

	public void setCeldasExploradas(int celdasExploradas) {
		this.celdasExploradas = celdasExploradas;
	}

	public long getTiempoEjecucion() {
		return tiempoEjecucion;
	}

	public void setTiempoEjecucion(long tiempoEjecucion) {
		this.tiempoEjecucion = tiempoEjecucion;
	}

	public String getAlgoritmo() {
		return algoritmo;
	}

	public void setAlgoritmo(String algoritmo) {
		this.algoritmo = algoritmo;
	}

	public boolean isExito() {
		return exito;
	}

	public void setExito(boolean exito) {
		this.exito = exito;
	}
}