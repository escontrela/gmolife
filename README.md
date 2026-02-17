# Game of Life

Game of Life is a visual application to explore Conway’s famous Game of Life. It lets you create, edit, and simulate cell patterns, observe their evolution, and use a built‑in AI search to discover interesting configurations without writing code.

## What you can do
- Simulate patterns with Play, Pause, and Step controls at your own pace.
- Adjust speed and zoom to see the detail you need.
- Change the board size and enable toroidal mode.
- Draw and edit cells directly on the grid.
- Load basic patterns like Glider or Blinker.
- Save and load your own patterns, copy/paste from the clipboard.
- Center the current pattern on the board.
- Export a PNG image of the board.
- Track generation, population, min/max/average, and TPS counters.
- View a population chart and export it to CSV.
- Run the AI search to find a promising pattern, see its fitness and preview.
- Cancel a running AI search and apply the best pattern with one click.

## Main controls
- Play: starts the automatic simulation.
- Pause: stops the simulation.
- Step: advances one generation.
- Reset: clears the board and resets counters.
- Randomize: generates a random starting board.
- Center: repositions the pattern to the board center.
- Speed: adjusts the time between generations.
- Zoom: changes cell size.
- Save/Load: saves patterns to a file and restores them later.
- Copy/Paste: sends the pattern to the clipboard or imports it from there.
- Export PNG: generates an image of the current board.
- Export CSV: downloads the population series shown in the chart.
- AI: sets parameters (population, generations, mutation) and runs the search.
- Apply AI: loads the AI‑suggested pattern into the board.

## Quick start (5–8 steps)
1. Choose a board size and adjust zoom if needed.
2. Draw some cells or select a basic pattern.
3. Press Play to see the evolution, or Step to advance manually.
4. Adjust speed while the simulation is running.
5. If you find an interesting state, use Save or Export PNG.
6. For automatic exploration, open the AI section and press Search.
7. Review the fitness and preview of the AI result.
8. If you like the pattern, press Apply AI and continue the simulation.

## Technical architecture
The UI is built with JavaFX and keeps rendering concerns separate from the simulation state. The grid view renders `GridState`, while higher‑level orchestration (play/pause, counters, exports, AI panel) lives in the main window controller.

`SimulationEngine` is the technical core for the AI search feature. It runs a genetic algorithm on a background single‑thread executor to avoid blocking the JavaFX UI thread. The engine:
- Generates a population of boolean grids and evaluates them over multiple steps.
- Scores candidates with configurable objectives (e.g., high population, oscillators, glider density).
- Evolves patterns via selection, crossover, and mutation.
- Supports cancellation through a `CancellationToken` and surfaces progress via a listener callback.
- Returns the best‑scoring pattern as an immutable result object.

This separation keeps the UI responsive while the search iterates, and makes the AI logic reusable and testable outside the JavaFX layer.

## Notes
- The application is designed for visual exploration and does not require technical knowledge.
- The AI search looks for patterns that keep an interesting population according to its fitness function.
