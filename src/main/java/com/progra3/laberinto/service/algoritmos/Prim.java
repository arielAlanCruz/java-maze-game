package com.progra3.laberinto.service.algoritmos;

import com.progra3.laberinto.model.Celda;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Prim implements AlgoritmoGeneracion {

	private final String nombre = "Prim";

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

		// Algoritmo de Prim para generar laberinto
		Random random = new Random();
		Set<String> visitados = new HashSet<>();
		List<Celda> frontera = new ArrayList<>();

		// Empezar desde una celda aleatoria (asegurando que sea impar para mantener
		// conectividad)
		int inicioX = 1 + random.nextInt((ancho - 1) / 2) * 2;
		int inicioY = 1 + random.nextInt((alto - 1) / 2) * 2;

		Celda inicio = grid.get(inicioY).get(inicioX);
		inicio.setTipo("LIBRE");
		visitados.add(inicio.getId());

		// Agregar vecinos a la frontera
		agregarVecinosAFrontera(grid, inicio, frontera, visitados, ancho, alto);

		while (!frontera.isEmpty()) {
			// Seleccionar celda aleatoria de la frontera
			Celda celdaActual = frontera.remove(random.nextInt(frontera.size()));

			if (!visitados.contains(celdaActual.getId())) {
				// Marcar como libre
				celdaActual.setTipo("LIBRE");
				visitados.add(celdaActual.getId());

				// Conectar con una celda visitada adyacente
				conectarConVecinoVisitado(grid, celdaActual, visitados, ancho, alto);

				// Agregar nuevos vecinos a la frontera
				agregarVecinosAFrontera(grid, celdaActual, frontera, visitados, ancho, alto);
			}
		}

		// Establecer celda de inicio y salida
		establecerInicioYSalida(grid, ancho, alto, laberintoId);

		return grid;
	}

	private void agregarVecinosAFrontera(List<List<Celda>> grid, Celda celda, List<Celda> frontera,
			Set<String> visitados, int ancho, int alto) {
		int[][] direcciones = { { 0, 2 }, { 2, 0 }, { 0, -2 }, { -2, 0 } }; // Saltos de 2 para mantener impar

		for (int[] dir : direcciones) {
			int nuevaX = celda.getX() + dir[0];
			int nuevaY = celda.getY() + dir[1];

			if (nuevaX >= 0 && nuevaX < ancho && nuevaY >= 0 && nuevaY < alto) {
				Celda vecino = grid.get(nuevaY).get(nuevaX);
				if (!visitados.contains(vecino.getId()) && !frontera.contains(vecino)) {
					frontera.add(vecino);
				}
			}
		}
	}

	private void conectarConVecinoVisitado(List<List<Celda>> grid, Celda celda, Set<String> visitados, int ancho,
			int alto) {
		int[][] direcciones = { { 0, 2 }, { 2, 0 }, { 0, -2 }, { -2, 0 } };
		List<Celda> vecinosVisitados = new ArrayList<>();

		// Encontrar vecinos visitados
		for (int[] dir : direcciones) {
			int nuevaX = celda.getX() + dir[0];
			int nuevaY = celda.getY() + dir[1];

			if (nuevaX >= 0 && nuevaX < ancho && nuevaY >= 0 && nuevaY < alto) {
				Celda vecino = grid.get(nuevaY).get(nuevaX);
				if (visitados.contains(vecino.getId())) {
					vecinosVisitados.add(vecino);
				}
			}
		}

		// Conectar con un vecino visitado aleatorio
		if (!vecinosVisitados.isEmpty()) {
			Random random = new Random();
			Celda vecinoElegido = vecinosVisitados.get(random.nextInt(vecinosVisitados.size()));

			// Crear pasillo entre las celdas
			int deltaX = Integer.compare(vecinoElegido.getX(), celda.getX());
			int deltaY = Integer.compare(vecinoElegido.getY(), celda.getY());

			int pasoX = celda.getX() + deltaX;
			int pasoY = celda.getY() + deltaY;

			if (pasoX >= 0 && pasoX < ancho && pasoY >= 0 && pasoY < alto) {
				grid.get(pasoY).get(pasoX).setTipo("LIBRE");
			}
		}
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

		// Si no hay suficientes celdas libres en los bordes, crear pasillos hacia los
		// bordes
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
}
