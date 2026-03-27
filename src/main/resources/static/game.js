class LaberintoGame {
  constructor() {
    this.scene = null;
    const self = this;

    this.config = {
      type: Phaser.AUTO,
      width: 800,
      height: 600,
      parent: "phaser-game",
      backgroundColor: "#2d3748",
      scene: {
        preload: function () {
          self.preload(this);
        },
        create: function () {
          self.create(this);
        },
        update: function (time, delta) {
          self.update(this, time, delta);
        },
      },
      scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH,
      },
    };

    this.game = new Phaser.Game(this.config);
    this.maze = null;
    this.solution = null;
    this.cellSize = 20;
    this.graphics = null;
    this.solutionPath = [];
    this.animatingPath = false;
    this.pathIndex = 0;
  }

  preload(scene) {
    this.scene = scene;
    // Phaser se encargará de cargar los recursos básicos
  }

  create(scene) {
    this.scene = scene;
    this.graphics = this.scene.add.graphics();
    this.showWelcomeMessage();
  }

  update(scene) {
    this.scene = scene;
    // Animación del camino de solución
    if (this.animatingPath && this.solutionPath.length > 0) {
      this.animateSolutionPath();
    }
  }

  showWelcomeMessage() {
    if (!this.scene || !this.graphics) {
      return;
    }

    this.graphics.clear();

    // Texto de bienvenida
    const welcomeText = this.scene.add
      .text(400, 250, "🏃‍♂️ Simulador de Laberintos", {
        fontSize: "32px",
        fill: "#ffffff",
        fontFamily: "Arial, sans-serif",
        align: "center",
      })
      .setOrigin(0.5);

    const instructionText = this.scene.add
      .text(400, 320, "Genera un laberinto para comenzar", {
        fontSize: "18px",
        fill: "#cbd5e0",
        fontFamily: "Arial, sans-serif",
        align: "center",
      })
      .setOrigin(0.5);

    // Animación de entrada
    welcomeText.setAlpha(0);
    instructionText.setAlpha(0);

    this.scene.tweens.add({
      targets: [welcomeText, instructionText],
      alpha: 1,
      duration: 1000,
      ease: "Power2",
    });

    this.welcomeText = welcomeText;
    this.instructionText = instructionText;
  }

  renderMaze(mazeData) {
    if (!this.scene || !this.graphics) {
      return;
    }

    // Limpiar contenido anterior
    this.graphics.clear();
    if (this.welcomeText) {
      this.welcomeText.destroy();
      this.instructionText.destroy();
    }

    this.maze = mazeData;
    this.solution = null;
    this.solutionPath = [];
    this.animatingPath = false;

    const grid = mazeData.grid;
    if (!grid || grid.length === 0) return;

    const rows = grid.length;
    const cols = grid[0].length;

    // Calcular tamaño de celda para que encaje en la pantalla
    const maxWidth = 760;
    const maxHeight = 560;
    this.cellSize = Math.min(maxWidth / cols, maxHeight / rows);

    // Centrar el laberinto
    const totalWidth = cols * this.cellSize;
    const totalHeight = rows * this.cellSize;
    const offsetX = (800 - totalWidth) / 2;
    const offsetY = (600 - totalHeight) / 2;

    // Dibujar cada celda
    for (let y = 0; y < rows; y++) {
      for (let x = 0; x < cols; x++) {
        const cell = grid[y][x];
        this.drawCell(x, y, cell, offsetX, offsetY);
      }
    }

    // Efecto de aparición
    this.scene.cameras.main.setAlpha(0);
    this.scene.tweens.add({
      targets: this.scene.cameras.main,
      alpha: 1,
      duration: 800,
      ease: "Power2",
    });
  }

  drawCell(x, y, cell, offsetX, offsetY) {
    const pixelX = offsetX + x * this.cellSize;
    const pixelY = offsetY + y * this.cellSize;

    let color;
    switch (cell.tipo) {
      case "MURO":
        color = 0x2d3748;
        break;
      case "LIBRE":
        color = 0xf7fafc;
        break;
      case "INICIO":
        color = 0x48bb78;
        break;
      case "SALIDA":
        color = 0xf56565;
        break;
      default:
        color = 0x718096;
    }

    this.graphics.fillStyle(color);
    this.graphics.fillRect(pixelX, pixelY, this.cellSize, this.cellSize);

    // Borde para mejor visualización
    this.graphics.lineStyle(1, 0x4a5568, 0.3);
    this.graphics.strokeRect(pixelX, pixelY, this.cellSize, this.cellSize);

    // Iconos para inicio y salida
    if (cell.tipo === "INICIO") {
      this.drawIcon(pixelX, pixelY, "🏁", 0x2d3748);
    } else if (cell.tipo === "SALIDA") {
      this.drawIcon(pixelX, pixelY, "🎯", 0xffffff);
    }
  }

  drawIcon(x, y, icon, color) {
    const iconText = this.scene.add
      .text(x + this.cellSize / 2, y + this.cellSize / 2, icon, {
        fontSize: `${Math.max(12, this.cellSize * 0.6)}px`,
        fill: `#${color.toString(16).padStart(6, "0")}`,
        align: "center",
      })
      .setOrigin(0.5);
  }

  renderSolution(solutionData) {
    if (!this.scene || !this.maze || !solutionData.camino) return;

    this.solution = solutionData;
    this.solutionPath = solutionData.camino.slice(); // Copia del array
    this.pathIndex = 0;
    this.animatingPath = true;

    // Efecto visual de inicio de resolución
    this.scene.cameras.main.flash(300, 255, 215, 0);
  }

  animateSolutionPath() {
    if (!this.scene) {
      return;
    }

    if (this.pathIndex >= this.solutionPath.length) {
      this.animatingPath = false;
      this.showSolutionComplete();
      return;
    }

    const cell = this.solutionPath[this.pathIndex];

    // Solo animar celdas libres (no inicio ni salida)
    if (cell.tipo === "LIBRE") {
      const grid = this.maze.grid;
      const rows = grid.length;
      const cols = grid[0].length;

      const maxWidth = 760;
      const maxHeight = 560;
      const cellSize = Math.min(maxWidth / cols, maxHeight / rows);

      const totalWidth = cols * cellSize;
      const totalHeight = rows * cellSize;
      const offsetX = (800 - totalWidth) / 2;
      const offsetY = (600 - totalHeight) / 2;

      const pixelX = offsetX + cell.x * cellSize;
      const pixelY = offsetY + cell.y * cellSize;

      // Dibujar celda de solución con efecto
      this.graphics.fillStyle(0xffd700);
      this.graphics.fillRect(pixelX, pixelY, cellSize, cellSize);

      // Efecto de partícula
      this.createParticleEffect(pixelX + cellSize / 2, pixelY + cellSize / 2);
    }

    this.pathIndex++;

    // Controlar velocidad de animación usando el temporizador de Phaser
    this.scene.time.delayedCall(100, () => {
      if (this.animatingPath) {
        this.animateSolutionPath();
      }
    });
  }

  createParticleEffect(x, y) {
    if (!this.scene) {
      return;
    }

    const particle = this.scene.add.graphics();
    particle.fillStyle(0xffd700);
    particle.fillCircle(0, 0, 3);
    particle.setPosition(x, y);
    particle.setAlpha(1);

    this.scene.tweens.add({
      targets: particle,
      alpha: 0,
      scaleX: 2,
      scaleY: 2,
      duration: 500,
      ease: "Power2",
      onComplete: () => particle.destroy(),
    });
  }

  showSolutionComplete() {
    if (!this.scene) {
      return;
    }

    // Efecto de completado
    this.scene.cameras.main.shake(300, 0.01);

    // Mostrar mensaje de éxito
    const successText = this.scene.add
      .text(400, 50, "✅ ¡Laberinto Resuelto!", {
        fontSize: "24px",
        fill: "#48bb78",
        fontFamily: "Arial, sans-serif",
        backgroundColor: "rgba(0,0,0,0.7)",
        padding: { x: 20, y: 10 },
      })
      .setOrigin(0.5);

    // Desaparecer después de 3 segundos
    this.scene.time.delayedCall(3000, () => {
      this.scene.tweens.add({
        targets: successText,
        alpha: 0,
        duration: 500,
        onComplete: () => successText.destroy(),
      });
    });
  }

  clearSolution() {
    if (!this.scene || !this.maze) return;

    this.animatingPath = false;
    this.solution = null;
    this.solutionPath = [];

    // Re-renderizar el laberinto sin la solución
    this.renderMaze(this.maze);

    // Efecto visual
    this.scene.cameras.main.fade(200, 45, 55, 72);
    this.scene.time.delayedCall(200, () => {
      this.scene.cameras.main.fadeIn(300);
    });
  }

  showError(message) {
    if (!this.scene) {
      return;
    }

    const errorText = this.scene.add
      .text(400, 300, `❌ Error: ${message}`, {
        fontSize: "18px",
        fill: "#f56565",
        fontFamily: "Arial, sans-serif",
        backgroundColor: "rgba(0,0,0,0.8)",
        padding: { x: 20, y: 15 },
        align: "center",
      })
      .setOrigin(0.5);

    // Auto-desaparecer
    this.scene.time.delayedCall(4000, () => {
      this.scene.tweens.add({
        targets: errorText,
        alpha: 0,
        y: errorText.y - 50,
        duration: 500,
        onComplete: () => errorText.destroy(),
      });
    });
  }

  showLoading(message = "Cargando...") {
    if (!this.scene) {
      return;
    }

    this.loadingText = this.scene.add
      .text(400, 300, message, {
        fontSize: "20px",
        fill: "#667eea",
        fontFamily: "Arial, sans-serif",
        backgroundColor: "rgba(0,0,0,0.8)",
        padding: { x: 20, y: 15 },
      })
      .setOrigin(0.5);

    // Animación pulsante
    this.scene.tweens.add({
      targets: this.loadingText,
      alpha: 0.5,
      duration: 800,
      yoyo: true,
      repeat: -1,
      ease: "Power2",
    });
  }

  hideLoading() {
    if (this.loadingText) {
      this.loadingText.destroy();
      this.loadingText = null;
    }
  }
}
