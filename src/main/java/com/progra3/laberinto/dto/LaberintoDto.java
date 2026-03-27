package com.progra3.laberinto.dto;

import com.progra3.laberinto.model.Celda;
import java.util.List;

public class LaberintoDto {
	private String id;
	private int ancho;
	private int alto;
	private List<List<Celda>> grid;
	private Celda celdaInicio;
	private Celda celdaSalida;

	// Constructores, Getters y Setters
	public LaberintoDto() {
	}

	public LaberintoDto(String id, int ancho, int alto) {
		this.id = id;
		this.ancho = ancho;
		this.alto = alto;
	}
	
	public LaberintoDto(String id, int ancho, int alto, List<List<Celda>> grid, Celda celdaInicio, Celda celdaSalida) {
		this.id = id;
		this.ancho = ancho;
		this.alto = alto;
		this.grid = grid;
		this.celdaInicio = celdaInicio;
		this.celdaSalida = celdaSalida;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public List<List<Celda>> getGrid() {
		return grid;
	}

	public void setGrid(List<List<Celda>> grid) {
		this.grid = grid;
	}

	public Celda getCeldaInicio() {
		return celdaInicio;
	}

	public void setCeldaInicio(Celda celdaInicio) {
		this.celdaInicio = celdaInicio;
	}

	public Celda getCeldaSalida() {
		return celdaSalida;
	}

	public void setCeldaSalida(Celda celdaSalida) {
		this.celdaSalida = celdaSalida;
	}
}