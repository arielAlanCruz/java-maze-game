class LaberintoAPI {
  constructor() {
    this.baseURL = "http://localhost:8080/api/laberinto";
    this.currentMazeId = null;
  }

  // Generar laberinto
  async generarLaberinto(ancho, alto, algoritmo) {
    try {
      const formData = new URLSearchParams();
      formData.append("ancho", ancho);
      formData.append("alto", alto);

      // Usar endpoints específicos según el algoritmo
      let endpoint;
      switch (algoritmo.toUpperCase()) {
        case "PRIM":
          endpoint = `${this.baseURL}/generar/prim`;
          break;
        case "KRUSKAL":
          endpoint = `${this.baseURL}/generar/kruskal`;
          break;
        default:
          throw new Error(`Algoritmo de generación no soportado: ${algoritmo}`);
      }

      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      const data = await response.json();
      this.currentMazeId = data.id;
      return data;
    } catch (error) {
      console.error("Error generando laberinto:", error);
      throw error;
    }
  }

  // Resolver laberinto
  async resolverLaberinto(algoritmo) {
    if (!this.currentMazeId) {
      throw new Error("No hay laberinto generado");
    }

    try {
      const formData = new URLSearchParams();
      formData.append("laberintoId", this.currentMazeId);

      // Usar endpoints específicos según el algoritmo
      let endpoint;
      switch (algoritmo.toUpperCase()) {
        case "BFS":
          endpoint = `${this.baseURL}/resolver/bfs`;
          break;
        case "DFS":
          endpoint = `${this.baseURL}/resolver/dfs`;
          break;
        case "DIJKSTRA":
          endpoint = `${this.baseURL}/resolver/dijkstra`;
          break;
        case "GREEDY":
          endpoint = `${this.baseURL}/resolver/greedy`;
          break;
		 case "BACKTRACKING":
			endpoint =  `${this.baseURL}/resolver/backtracking`;
			break;
        default:
          throw new Error(`Algoritmo de resolución no soportado: ${algoritmo}`);
      }

      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error resolviendo laberinto:", error);
      throw error;
    }
  }

  // Obtener algoritmos disponibles
  async obtenerAlgoritmos() {
    try {
      const response = await fetch(`${this.baseURL}/algoritmos`);
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Error obteniendo algoritmos:", error);
      throw error;
    }
  }

  // Comparar todos los algoritmos y obtener ranking
  async resolverTodos(metric = "PATH", sorter = "MERGE") {
    if (!this.currentMazeId) {
      throw new Error("No hay laberinto generado");
    }
    try {
      const formData = new URLSearchParams();
      formData.append("laberintoId", this.currentMazeId);
      formData.append("metric", metric);
      formData.append("sorter", sorter);

      const response = await fetch(`${this.baseURL}/resolver/all`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData,
      });
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      console.error("Error comparando algoritmos:", error);
      throw error;
    }
  }

  // Diagnosticar laberinto
  async diagnosticarLaberinto() {
    if (!this.currentMazeId) {
      throw new Error("No hay laberinto generado");
    }

    try {
      const response = await fetch(
        `${this.baseURL}/diagnostico/${this.currentMazeId}`
      );
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      return await response.text();
    } catch (error) {
      console.error("Error diagnosticando laberinto:", error);
      throw error;
    }
  }
}

// Instancia global de la API
const api = new LaberintoAPI();
