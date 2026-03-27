package com.progra3.laberinto.model;

import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Celda")
public class Celda {
	@Id
	private String id; // formato: "laberintoId-x-y"
	@Version
	private Long version;
	private int x;
	private int y;
	private String tipo; // "LIBRE", "MURO", "INICIO", "SALIDA"
	private String laberintoId;

	// Constructores
	public Celda() {
	}

	public Celda(String id, int x, int y, String tipo, String laberintoId) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.tipo = tipo;
		this.laberintoId = laberintoId;
	}

	// Getters y Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getLaberintoId() {
		return laberintoId;
	}

	public void setLaberintoId(String laberintoId) {
		this.laberintoId = laberintoId;
	}
}