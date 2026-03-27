// Variables globales
let game = null;
let currentMazeData = null;

// Inicializar cuando el DOM esté cargado
document.addEventListener("DOMContentLoaded", function () {
  initializeGame();
  setupEventListeners();
  loadAvailableAlgorithms();
});

// Inicializar el juego Phaser
function initializeGame() {
  game = new LaberintoGame();
  console.log("🎮 Simulador de laberintos iniciado");
}

// Configurar event listeners
function setupEventListeners() {
  // Botón generar
  document
    .getElementById("generate-btn")
    .addEventListener("click", generateMaze);

  // Botón resolver
  document.getElementById("solve-btn").addEventListener("click", solveMaze);

  // Botón limpiar
  document.getElementById("clear-btn").addEventListener("click", clearSolution);

  // Botón comparar
  const compareBtn = document.getElementById("compare-btn");
  if (compareBtn) {
    compareBtn.addEventListener("click", compareAlgorithms);
  }

  // Validación de inputs
  setupInputValidation();
}

// Configurar validación de inputs
function setupInputValidation() {
  const widthInput = document.getElementById("width");
  const heightInput = document.getElementById("height");

  // Asegurar que sean números impares (para algoritmos de laberinto)
  [widthInput, heightInput].forEach((input) => {
    input.addEventListener("change", function () {
      let value = parseInt(this.value);
      if (value % 2 === 0) {
        value = value + 1; // Convertir a impar
      }
      if (value < 5) value = 5;
      if (value > 50) value = 49; // Corregir límite máximo
      this.value = value;
    });
  });
}

// Cargar algoritmos disponibles desde el backend
async function loadAvailableAlgorithms() {
  try {
    const algorithms = await api.obtenerAlgoritmos();
    console.log("Algoritmos disponibles:", algorithms);

    // Actualizar selector de algoritmos de resolución
    const solverSelect = document.getElementById("solver");
    solverSelect.innerHTML = "";

    // Filtrar solo algoritmos de resolución
    const solverAlgorithms = algorithms.filter((alg) =>
      ["BFS", "DFS", "DIJKSTRA", "GREEDY", "BACKTRACKING"].includes(alg)
    );

    solverAlgorithms.forEach((algorithm) => {
      const option = document.createElement("option");
      option.value = algorithm;
      option.textContent = getAlgorithmDisplayName(algorithm);
      solverSelect.appendChild(option);
    });
  } catch (error) {
    console.error("Error cargando algoritmos:", error);
    showNotification("Error conectando con el servidor", "error");
  }
}

// Obtener nombre de display para algoritmos
function getAlgorithmDisplayName(algorithm) {
  const names = {
    BFS: "BFS (Breadth-First Search)",
    DFS: "DFS (Depth-First Search)",
    DIJKSTRA: "Dijkstra",
    GREEDY: "Greedy (Best-First)",
    PRIM: "Prim",
    KRUSKAL: "Kruskal",
	BACKTRACKING : "Backtracking",
  };
  return names[algorithm] || algorithm;
}

// Generar laberinto
async function generateMaze() {
  const width = parseInt(document.getElementById("width").value);
  const height = parseInt(document.getElementById("height").value);
  const algorithm = document.getElementById("algorithm").value;

  const generateBtn = document.getElementById("generate-btn");
  const solveBtn = document.getElementById("solve-btn");

  try {
    // UI Loading state
    generateBtn.textContent = "⏳ Generando...";
    generateBtn.disabled = true;
    generateBtn.classList.add("loading");
    solveBtn.disabled = true;

    updateStatus("Generando laberinto...", "info");
    game.showLoading("🏗️ Generando laberinto...");

    // Llamar API
    console.log(`Generando laberinto ${width}x${height} con ${algorithm}`);
    const mazeData = await api.generarLaberinto(width, height, algorithm);

    console.log("Laberinto generado:", mazeData);
    currentMazeData = mazeData;

    // Actualizar UI
    updateMazeInfo(mazeData, algorithm);
    game.hideLoading();
    game.renderMaze(mazeData);

    // Habilitar botón resolver
    solveBtn.disabled = false;
    // Habilitar botón comparar
    const compareBtn = document.getElementById("compare-btn");
    if (compareBtn) compareBtn.disabled = false;
    updateStatus("Laberinto generado exitosamente", "success");

    showNotification("✅ Laberinto generado correctamente", "success");
  } catch (error) {
    console.error("Error generando laberinto:", error);
    game.hideLoading();
    game.showError("No se pudo generar el laberinto");
    updateStatus("Error generando laberinto", "error");
    showNotification("❌ Error generando laberinto", "error");
  } finally {
    // Restaurar botón
    generateBtn.textContent = "🎲 Generar";
    generateBtn.disabled = false;
    generateBtn.classList.remove("loading");
  }
}

// Comparar todos los algoritmos
async function compareAlgorithms() {
  if (!currentMazeData) {
    showNotification("⚠️ Primero genera un laberinto", "warning");
    return;
  }
  const metric = document.getElementById("metric-select").value;
  const sorter = document.getElementById("sorter-select").value;
  const compareBtn = document.getElementById("compare-btn");

  try {
    compareBtn.textContent = "📊 Comparando...";
    compareBtn.disabled = true;
    updateStatus("Comparando algoritmos...", "info");
    game.showLoading("📊 Comparando algoritmos...");

    const ranking = await api.resolverTodos(metric, sorter);
    console.log("Ranking recibido:", ranking);
    renderRanking(ranking);
    updateStatus("Comparación completada", "success");
    showNotification("✅ Comparación lista", "success");
  } catch (err) {
    console.error("Error comparando algoritmos", err);
    updateStatus("Error comparando", "error");
    showNotification("❌ Error comparando", "error");
  } finally {
    compareBtn.textContent = "📈 Comparar todos";
    compareBtn.disabled = false;
    game.hideLoading();
  }
}

function renderRanking(ranking) {
  const container = document.getElementById("ranking-container");
  const tbody = document.querySelector("#ranking-table tbody");
  if (!container || !tbody) return;
  tbody.innerHTML = "";

  ranking.forEach((r) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${getAlgorithmDisplayName(r.algoritmo)}</td>
      <td>${r.largoCamino}</td>
      <td>${r.celdasExploradas}</td>
      <td>${r.tiempoEjecucionMs}</td>
      <td>${r.exito ? "✔️" : "❌"}</td>
    `;
    tbody.appendChild(tr);
  });
  container.style.display = "block";
}

// Resolver laberinto
async function solveMaze() {
  if (!currentMazeData) {
    showNotification("⚠️ Primero debes generar un laberinto", "warning");
    return;
  }

  const algorithm = document.getElementById("solver").value;
  const solveBtn = document.getElementById("solve-btn");

  try {
    // UI Loading state
    solveBtn.textContent = "⚡ Resolviendo...";
    solveBtn.disabled = true;
    solveBtn.classList.add("loading");

    updateStatus("Resolviendo laberinto...", "info");
    game.showLoading("🧠 Resolviendo laberinto...");

    // Llamar API
    console.log(`Resolviendo laberinto con ${algorithm}`);
    const solutionData = await api.resolverLaberinto(algorithm);

    console.log("Solución encontrada:", solutionData);

    // Actualizar UI
    updateSolutionInfo(solutionData);
    game.hideLoading();

    if (solutionData.exito && solutionData.camino.length > 0) {
      game.renderSolution(solutionData);
      updateStatus("Laberinto resuelto exitosamente", "success");
      showNotification("✅ ¡Solución encontrada!", "success");
    } else {
      game.showError("No se encontró solución");
      updateStatus("No se encontró solución", "warning");
      showNotification("⚠️ No se encontró solución", "warning");
    }
  } catch (error) {
    console.error("Error resolviendo laberinto:", error);
    game.hideLoading();
    game.showError("No se pudo resolver el laberinto");
    updateStatus("Error resolviendo laberinto", "error");
    showNotification("❌ Error resolviendo laberinto", "error");
  } finally {
    // Restaurar botón
    solveBtn.textContent = "⚡ Resolver";
    solveBtn.disabled = false;
    solveBtn.classList.remove("loading");
  }
}

// Limpiar solución
function clearSolution() {
  if (game && currentMazeData) {
    game.clearSolution();
    clearSolutionInfo();
    updateStatus("Solución limpiada", "info");
    showNotification("🧹 Solución limpiada", "info");
  }
}

// Actualizar información del laberinto
function updateMazeInfo(mazeData, algorithm) {
  document.getElementById("maze-id").textContent =
    mazeData.id.substring(0, 8) + "...";
  document.getElementById(
    "maze-size"
  ).textContent = `${mazeData.ancho}x${mazeData.alto}`;
  document.getElementById("used-algorithm").textContent =
    getAlgorithmDisplayName(algorithm);

  // Limpiar métricas anteriores
  clearSolutionInfo();
}

// Actualizar información de la solución
function updateSolutionInfo(solutionData) {
  document.getElementById("cells-explored").textContent =
    solutionData.celdasExploradas || "-";
  document.getElementById("execution-time").textContent =
    solutionData.tiempoEjecucion || "-";
  document.getElementById("used-algorithm").textContent =
    getAlgorithmDisplayName(solutionData.algoritmo);
}

// Limpiar información de la solución
function clearSolutionInfo() {
  document.getElementById("cells-explored").textContent = "-";
  document.getElementById("execution-time").textContent = "-";
}

// Actualizar estado
function updateStatus(message, type) {
  const statusElement = document.getElementById("status");
  statusElement.textContent = message;
  statusElement.className = `status-${type}`;
}

// Mostrar notificaciones
function showNotification(message, type) {
  // Crear elemento de notificación
  const notification = document.createElement("div");
  notification.className = `notification notification-${type}`;
  notification.textContent = message;

  // Estilos inline para la notificación
  Object.assign(notification.style, {
    position: "fixed",
    top: "20px",
    right: "20px",
    padding: "15px 20px",
    borderRadius: "8px",
    color: "white",
    fontWeight: "bold",
    zIndex: "9999",
    maxWidth: "300px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
    transform: "translateX(100%)",
    transition: "transform 0.3s ease",
  });

  // Colores según tipo
  const colors = {
    success: "#48bb78",
    error: "#f56565",
    warning: "#ed8936",
    info: "#667eea",
  };
  notification.style.backgroundColor = colors[type] || colors.info;

  // Agregar al DOM
  document.body.appendChild(notification);

  // Animar entrada
  setTimeout(() => {
    notification.style.transform = "translateX(0)";
  }, 10);

  // Auto-remover después de 4 segundos
  setTimeout(() => {
    notification.style.transform = "translateX(100%)";
    setTimeout(() => {
      if (notification.parentNode) {
        notification.parentNode.removeChild(notification);
      }
    }, 300);
  }, 4000);
}

// Manejo de errores globales
window.addEventListener("error", function (event) {
  console.error("Error global:", event.error);
  showNotification("❌ Error inesperado", "error");
});

// Logs de estado
console.log("🚀 Simulador de laberintos cargado");
console.log("🔗 Conectando con backend en:", api.baseURL);
console.log(
  "✅ Frontend actualizado para nuevos endpoints específicos por algoritmo"
);
console.log("📍 Endpoints de generación: /generar/prim, /generar/kruskal");
console.log(
  "📍 Endpoints de resolución: /resolver/bfs, /resolver/dfs, /resolver/dijkstra, /resolver/greedy"
);
