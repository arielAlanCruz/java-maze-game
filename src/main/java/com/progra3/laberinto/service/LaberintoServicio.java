package com.progra3.laberinto.service;

import com.progra3.laberinto.model.Celda;
import com.progra3.laberinto.model.Laberinto;
import com.progra3.laberinto.service.algoritmos.AlgoritmoServicioImpl;
import com.progra3.laberinto.service.algoritmos.Prim;
import com.progra3.laberinto.service.algoritmos.Kruskal;
import com.progra3.laberinto.excepciones.AlgoritmoNoSoportadoException;
import com.progra3.laberinto.excepciones.LaberintoNoEncontradoException;
import com.progra3.laberinto.dto.AlgoritmoResultadoDto;
import com.progra3.laberinto.service.algoritmos.MergeSort;
import com.progra3.laberinto.service.algoritmos.QuickSort;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LaberintoServicio {
	
	//esto es inyección de dependencias por constructor
	private final AlgoritmoServicioImpl algoritmoServicio;
	private final Neo4jServicioReactivo neo4jServicio;
	private final Prim prim;
	private final Kruskal kruskal;

	public LaberintoServicio(AlgoritmoServicioImpl algoritmoServicio, Neo4jServicioReactivo neo4jServicio, Prim prim,
			Kruskal kruskal) {
		this.algoritmoServicio = algoritmoServicio;
		this.neo4jServicio = neo4jServicio;
		this.prim = prim;
		this.kruskal = kruskal;
	}
	
	public Laberinto generarLaberinto(int ancho, int alto, String algoritmo) {
		System.out.println("=== INICIANDO GENERACIÓN LABERINTO ===");
		// Crear instancia de laberinto
		Laberinto laberinto = new Laberinto(UUID.randomUUID().toString(), ancho, alto);
		System.out.println("Laberinto creado con ID: " + laberinto.getId());
		
		// Generar grid usando algoritmo específico
		List<List<Celda>> grid;
		switch (algoritmo.toUpperCase()) {
		case "PRIM":
			grid = prim.generarLaberinto(ancho, alto, laberinto.getId());
			break;
		case "KRUSKAL":
			grid = kruskal.generarLaberinto(ancho, alto, laberinto.getId());
			break;
		default:
			throw new AlgoritmoNoSoportadoException(algoritmo);
		}
		laberinto.setGrid(grid);
		// Encontrar y establecer celdas de inicio y salida
		Celda inicio = null;
		Celda salida = null;
		for (List<Celda> fila : grid) {
			for (Celda celda : fila) {
				if ("INICIO".equals(celda.getTipo())) {
					inicio = celda;
				} else if ("SALIDA".equals(celda.getTipo())) {
					salida = celda;
				}
			}
		}

		laberinto.setCeldaInicio(inicio);
		laberinto.setCeldaSalida(salida);

		// Guardar en Neo4j
		List<Celda> todasLasCeldas = new ArrayList<>();
		grid.forEach(todasLasCeldas::addAll);
		System.out.println("Total de celdas a guardar: " + todasLasCeldas.size());

		// Contar tipos de celdas
		long celdasInicio = todasLasCeldas.stream().filter(c -> "INICIO".equals(c.getTipo())).count();
		long celdasSalida = todasLasCeldas.stream().filter(c -> "SALIDA".equals(c.getTipo())).count();
		long celdasLibres = todasLasCeldas.stream().filter(c -> "LIBRE".equals(c.getTipo())).count();
		long celdasMuro = todasLasCeldas.stream().filter(c -> "MURO".equals(c.getTipo())).count();

		System.out.println("Tipos de celdas:");
		System.out.println("- INICIO: " + celdasInicio);
		System.out.println("- SALIDA: " + celdasSalida);
		System.out.println("- LIBRE: " + celdasLibres);
		System.out.println("- MURO: " + celdasMuro);

		try {
			neo4jServicio.guardarTodasLasCeldas(todasLasCeldas).blockLast();
			System.out.println("Celdas guardadas exitosamente en Neo4j");

			// Verificar que se guardaron correctamente
			System.out.println("Verificando guardado...");
			List<Celda> celdasVerificacion = neo4jServicio.obtenerCeldasPorLaberinto(laberinto.getId()).collectList()
					.block();
			System.out.println("Celdas recuperadas de Neo4j: "
					+ (celdasVerificacion != null ? celdasVerificacion.size() : "null"));

			// Asegurar consistencia: obtener inicio y salida desde la BD y asignarlos al objeto laberinto
			try {
				Celda inicioDb = neo4jServicio.obtenerCeldaInicio(laberinto.getId()).block();
				Celda salidaDb = neo4jServicio.obtenerCeldaSalida(laberinto.getId()).block();
				if (inicioDb != null) {
					laberinto.setCeldaInicio(inicioDb);
				}
				if (salidaDb != null) {
					laberinto.setCeldaSalida(salidaDb);
				}
				System.out.println("Inicio en BD: " + (inicioDb != null ? "(" + inicioDb.getX() + "," + inicioDb.getY() + ")" : "NO"));
				System.out.println("Salida en BD: " + (salidaDb != null ? "(" + salidaDb.getX() + "," + salidaDb.getY() + ")" : "NO"));
			} catch (Exception ex) {
				System.out.println("ERROR obteniendo inicio/salida desde BD: " + ex.getMessage());
				ex.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("ERROR guardando en Neo4j: " + e.getMessage());
			e.printStackTrace();
		}

		return laberinto;
	}

	public List<Celda> resolverLaberinto(String laberintoId, String algoritmo) {
		System.out.println("=== INICIANDO RESOLUCIÓN LABERINTO ===");
		System.out.println("Laberinto ID: " + laberintoId);
		System.out.println("Algoritmo: " + algoritmo);

		// Cargar laberinto de Neo4j
		System.out.println("Buscando celdas en Neo4j...");
		List<Celda> celdas = neo4jServicio.obtenerCeldasPorLaberinto(laberintoId).collectList().block();
		System.out.println("Celdas encontradas: " + (celdas != null ? celdas.size() : "null"));

		if (celdas == null || celdas.isEmpty()) {
			System.out.println("ERROR: No se encontraron celdas para el laberinto ID: " + laberintoId);
			throw new LaberintoNoEncontradoException(laberintoId);
		}

		System.out.println("Buscando celda de inicio...");
		Celda inicio = neo4jServicio.obtenerCeldaInicio(laberintoId).block();
		System.out.println("Celda inicio encontrada: " + (inicio != null ? "SÍ" : "NO"));

		System.out.println("Buscando celda de salida...");
		Celda salida = neo4jServicio.obtenerCeldaSalida(laberintoId).block();
		System.out.println("Celda salida encontrada: " + (salida != null ? "SÍ" : "NO"));

		if (inicio == null || salida == null) {
			System.out
					.println("ERROR: No se encontraron celdas de inicio o salida para el laberinto ID: " + laberintoId);
			throw new LaberintoNoEncontradoException(laberintoId);
		}

		System.out.println("Ejecutando algoritmo: " + algoritmo);
		// Ejecutar algoritmo de resolución
		switch (algoritmo.toUpperCase()) {
		case "BFS":
			return algoritmoServicio.bfs(inicio, salida, convertirAGrid(celdas));
		case "DFS":
			return algoritmoServicio.dfs(inicio, salida, convertirAGrid(celdas));
		case "DIJKSTRA":
			return algoritmoServicio.dijkstra(inicio, salida, convertirAGrid(celdas));
		case "GREEDY":
			return algoritmoServicio.greedy(inicio, salida, convertirAGrid(celdas));
		case "BACKTRACKING":
			return algoritmoServicio.backtracking(inicio, salida, convertirAGrid(celdas));
		default:
			throw new AlgoritmoNoSoportadoException(algoritmo);
		}
	}

	// Método para obtener métricas de un algoritmo específico
	public int obtenerCeldasExploradas(String algoritmo) {
		switch (algoritmo.toUpperCase()) {
		case "BFS":
			return algoritmoServicio.getBfs().getCeldasExploradas();
		case "DFS":
			return algoritmoServicio.getDfs().getCeldasExploradas();
		case "DIJKSTRA":
			return algoritmoServicio.getDijkstra().getCeldasExploradas();
		case "GREEDY":
			return algoritmoServicio.getGreedy().getCeldasExploradas();
		case "BACKTRACKING":
			return algoritmoServicio.getBacktracking().getCeldasExploradas();
		default:
			return 0;
		}
	}

	public long obtenerTiempoEjecucion(String algoritmo) {
		switch (algoritmo.toUpperCase()) {
		case "BFS":
			return algoritmoServicio.getBfs().getTiempoEjecucion();
		case "DFS":
			return algoritmoServicio.getDfs().getTiempoEjecucion();
		case "DIJKSTRA":
			return algoritmoServicio.getDijkstra().getTiempoEjecucion();
		case "GREEDY":
			return algoritmoServicio.getGreedy().getTiempoEjecucion();
		case "BACKTRACKING":
			return algoritmoServicio.getBacktracking().getTiempoEjecucion();
		default:
			return 0L;
		}
	}

	public List<AlgoritmoResultadoDto> compararAlgoritmos(String laberintoId, String metric, String sorter) {
		List<String> algoritmos = Arrays.asList("BFS", "DFS", "DIJKSTRA", "GREEDY", "BACKTRACKING");
		List<AlgoritmoResultadoDto> resultados = new ArrayList<>();

		for (String alg : algoritmos) {
			List<Celda> camino = resolverLaberinto(laberintoId, alg);
			boolean exito = camino != null && !camino.isEmpty();
			int largo = (camino != null && exito) ? camino.size() : 0;
			int exploradas = obtenerCeldasExploradas(alg);
			long tiempo = obtenerTiempoEjecucion(alg);
			resultados.add(new AlgoritmoResultadoDto(alg, largo, exploradas, tiempo, exito));
		}

		Comparator<AlgoritmoResultadoDto> comparator;
		switch ((metric == null) ? "PATH" : metric.toUpperCase()) {
		case "TIME":
			comparator = (a, b) -> Long.compare(a.getTiempoEjecucionMs(), b.getTiempoEjecucionMs());
			break;
		case "EXPLORED":
			comparator = (a, b) -> Integer.compare(a.getCeldasExploradas(), b.getCeldasExploradas());
			break;
		case "PATH":
		default:
			comparator = (a, b) -> Integer.compare(a.getLargoCamino(), b.getLargoCamino());
			break;
		}

		List<AlgoritmoResultadoDto> ordenados;
		switch ((sorter == null) ? "MERGE" : sorter.toUpperCase()) {
		case "QUICK":
			ordenados = QuickSort.sort(resultados, comparator);
			break;
		case "MERGE":
		default:
			ordenados = MergeSort.sort(resultados, comparator);
			break;
		}

		return ordenados;
	}

	// Getter para acceso desde el controller
	public Neo4jServicioReactivo getNeo4jServicio() {
		return neo4jServicio;
	}

	private List<List<Celda>> convertirAGrid(List<Celda> celdas) {
		if (celdas == null || celdas.isEmpty()) {
			return new ArrayList<>();
		}

		// Encontrar las dimensiones del grid de manera más eficiente
		int maxX = 0, maxY = 0;
		String laberintoId = null;
		for (Celda celda : celdas) {
			if (celda == null) continue;
			maxX = Math.max(maxX, celda.getX());
			maxY = Math.max(maxY, celda.getY());
			if (laberintoId == null && celda.getLaberintoId() != null) {
				laberintoId = celda.getLaberintoId();
			}
		}

		// Crear grid con dimensiones exactas
		List<List<Celda>> grid = new ArrayList<>(maxY + 1);
		for (int y = 0; y <= maxY; y++) {
			List<Celda> fila = new ArrayList<>(maxX + 1);
			for (int x = 0; x <= maxX; x++) {
				fila.add(null);
			}
			grid.add(fila);
		}

		// Llenar el grid con las celdas en una sola pasada
		for (Celda celda : celdas) {
			if (celda == null) continue;
			int x = celda.getX();
			int y = celda.getY();
			if (y >= 0 && y <= maxY && x >= 0 && x <= maxX) {
				grid.get(y).set(x, celda);
			} else {
				System.out.println("ADVERTENCIA: Celda con coordenadas inválidas encontrada: " +
					(celda.getLaberintoId() != null ? celda.getLaberintoId() : "?") + " - x=" + x + " y=" + y);
			}
		}

		// Rellenar celdas faltantes con muros (placeholders) para evitar NPE en algoritmos de resolución
		if (laberintoId == null && !celdas.isEmpty()) {
			laberintoId = celdas.get(0).getLaberintoId();
		}
		for (int y = 0; y <= maxY; y++) {
			for (int x = 0; x <= maxX; x++) {
				if (grid.get(y).get(x) == null) {
					String id = (laberintoId != null ? laberintoId : "unknown") + "-" + x + "-" + y;
					Celda placeholder = new Celda(id, x, y, "MURO", laberintoId);
					grid.get(y).set(x, placeholder);
					System.out.println("INFO: Insertando celda placeholder MURO en posición (" + x + "," + y + ")");
				}
			}
		}

		return grid;
	}
}