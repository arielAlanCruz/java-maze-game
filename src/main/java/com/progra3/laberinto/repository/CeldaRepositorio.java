package com.progra3.laberinto.repository;

import com.progra3.laberinto.model.Celda;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CeldaRepositorio extends ReactiveNeo4jRepository<Celda, String> {
    
    @Query("MATCH (c:Celda) WHERE c.laberintoId = $laberintoId RETURN c ORDER BY c.y, c.x")
    Flux<Celda> findByLaberintoId(String laberintoId);
    
    @Query("MATCH (c:Celda) WHERE c.laberintoId = $laberintoId AND c.x = $x AND c.y = $y RETURN c")
    Mono<Celda> findByPosicion(String laberintoId, int x, int y);
    
    @Query("MATCH (c:Celda) WHERE c.laberintoId = $laberintoId AND c.tipo = 'INICIO' RETURN c")
    Mono<Celda> findCeldaInicio(String laberintoId);
    
    @Query("MATCH (c:Celda) WHERE c.laberintoId = $laberintoId AND c.tipo = 'SALIDA' RETURN c")
    Mono<Celda> findCeldaSalida(String laberintoId);
}