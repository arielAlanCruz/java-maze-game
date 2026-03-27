package com.progra3.laberinto.service;

import com.progra3.laberinto.model.Celda;
import com.progra3.laberinto.repository.CeldaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class Neo4jServicioReactivo {

	private final CeldaRepositorio celdaRepositorio;
	
	public Neo4jServicioReactivo(CeldaRepositorio celdaRepositorio) {
		this.celdaRepositorio = celdaRepositorio;
	}

	public Mono<Celda> guardarCelda(Celda celda) {
		return celdaRepositorio.save(celda);
	}

	public Flux<Celda> guardarTodasLasCeldas(Iterable<Celda> celdas) {
		return celdaRepositorio.saveAll(celdas);
	}

	public Flux<Celda> obtenerCeldasPorLaberinto(String laberintoId) {
		return celdaRepositorio.findByLaberintoId(laberintoId);
	}

	public Mono<Celda> obtenerCeldaInicio(String laberintoId) {
		return celdaRepositorio.findCeldaInicio(laberintoId);
	}

	public Mono<Celda> obtenerCeldaSalida(String laberintoId) {
		return celdaRepositorio.findCeldaSalida(laberintoId);
	}
}