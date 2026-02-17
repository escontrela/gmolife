package com.davidpe.gmolife.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SimulationEngine {

  private final ExecutorService executor;
  private final Random random = new Random();

  public SimulationEngine() {
    this.executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
  }

  public boolean isStarted() {
    return false;
  }

  public CompletableFuture<GeneticSearchResult> findPromisingPattern(GeneticSearchConfig config) {
    Objects.requireNonNull(config, "config");
    return findPromisingPattern(config, null);
  }

  public CompletableFuture<GeneticSearchResult> findPromisingPattern(
      GeneticSearchConfig config, CancellationToken token) {
    return findPromisingPattern(config, token, null);
  }

  public CompletableFuture<GeneticSearchResult> findPromisingPattern(
      GeneticSearchConfig config, CancellationToken token, GeneticSearchProgressListener listener) {
    Objects.requireNonNull(config, "config");
    return CompletableFuture.supplyAsync(
        () -> runGeneticSearch(config, token, listener), executor);
  }

  public void shutdown() {
    executor.shutdownNow();
  }

  private GeneticSearchResult runGeneticSearch(
      GeneticSearchConfig config, CancellationToken token, GeneticSearchProgressListener listener) {
    List<boolean[][]> population = new ArrayList<>(config.populationSize());
    for (int i = 0; i < config.populationSize(); i++) {
      checkCancelled(token);
      population.add(randomPattern(config.rows(), config.columns()));
    }

    boolean[][] bestPattern = null;
    double bestFitness = Double.NEGATIVE_INFINITY;

    for (int generation = 0; generation < config.generations(); generation++) {
      checkCancelled(token);
      List<ScoredPattern> scored =
          scorePopulation(population, config.evaluationSteps(), config.objective(), token);
      for (ScoredPattern entry : scored) {
        if (entry.fitness > bestFitness) {
          bestFitness = entry.fitness;
          bestPattern = copyPattern(entry.pattern);
        }
      }
      if (listener != null) {
        listener.onProgress(generation + 1, bestFitness);
      }
      List<boolean[][]> next = new ArrayList<>(config.populationSize());
      next.add(copyPattern(scored.get(0).pattern));
      while (next.size() < config.populationSize()) {
        checkCancelled(token);
        boolean[][] parentA = select(scored);
        boolean[][] parentB = select(scored);
        boolean[][] child = crossover(parentA, parentB, config.crossoverRate());
        mutate(child, config.mutationRate());
        next.add(child);
      }
      population = next;
    }

    if (bestPattern == null) {
      bestPattern = randomPattern(config.rows(), config.columns());
      bestFitness = scorePattern(bestPattern, config.evaluationSteps(), config.objective(), token);
    }
    return new GeneticSearchResult(bestPattern, bestFitness);
  }

  @FunctionalInterface
  public interface GeneticSearchProgressListener {
    void onProgress(int generation, double bestFitness);
  }

  private List<ScoredPattern> scorePopulation(
      List<boolean[][]> population,
      int evaluationSteps,
      GeneticObjective objective,
      CancellationToken token) {
    List<ScoredPattern> scored = new ArrayList<>(population.size());
    for (boolean[][] pattern : population) {
      scored.add(new ScoredPattern(pattern, scorePattern(pattern, evaluationSteps, objective, token)));
    }
    scored.sort((a, b) -> Double.compare(b.fitness, a.fitness));
    return scored;
  }

  private double scorePattern(
      boolean[][] pattern,
      int evaluationSteps,
      GeneticObjective objective,
      CancellationToken token) {
    return switch (objective) {
      case HIGH_POPULATION -> scoreHighPopulation(pattern, evaluationSteps, token);
      case OSCILLATOR -> scoreOscillator(pattern, evaluationSteps, token);
      case GLIDER -> scoreGlider(pattern, evaluationSteps, token);
    };
  }

  private double scoreHighPopulation(
      boolean[][] pattern, int evaluationSteps, CancellationToken token) {
    boolean[][] current = copyPattern(pattern);
    double total = 0;
    for (int step = 0; step < evaluationSteps; step++) {
      checkCancelled(token);
      total += countAlive(current);
      current = advance(current);
    }
    return total / evaluationSteps;
  }

  private double scoreOscillator(boolean[][] pattern, int evaluationSteps, CancellationToken token) {
    boolean[][] current = copyPattern(pattern);
    List<String> signatures = new ArrayList<>(evaluationSteps);
    double total = 0;
    for (int step = 0; step < evaluationSteps; step++) {
      checkCancelled(token);
      total += countAlive(current);
      String signature = signatureFor(current);
      for (int prev = 0; prev < signatures.size(); prev++) {
        if (signature.equals(signatures.get(prev))) {
          int period = (step + 1) - (prev + 1);
          if (period >= 2) {
            double average = total / (step + 1);
            return 800 + average - period * 10;
          }
        }
      }
      signatures.add(signature);
      current = advance(current);
    }
    return (total / evaluationSteps) * 0.6;
  }

  private double scoreGlider(boolean[][] pattern, int evaluationSteps, CancellationToken token) {
    boolean[][] current = copyPattern(pattern);
    double total = 0;
    int gliderScore = 0;
    for (int step = 0; step < evaluationSteps; step++) {
      checkCancelled(token);
      total += countAlive(current);
      gliderScore += countGliders(current) * 100;
      current = advance(current);
    }
    double average = total / evaluationSteps;
    return gliderScore + average;
  }

  private boolean[][] select(List<ScoredPattern> scored) {
    int tournamentSize = Math.min(3, scored.size());
    ScoredPattern best = scored.get(random.nextInt(scored.size()));
    for (int i = 1; i < tournamentSize; i++) {
      ScoredPattern candidate = scored.get(random.nextInt(scored.size()));
      if (candidate.fitness > best.fitness) {
        best = candidate;
      }
    }
    return best.pattern;
  }

  private boolean[][] crossover(boolean[][] parentA, boolean[][] parentB, double rate) {
    if (random.nextDouble() >= rate) {
      return copyPattern(parentA);
    }
    int rows = parentA.length;
    int columns = parentA[0].length;
    int splitRow = random.nextInt(rows);
    boolean[][] child = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      boolean[][] source = row <= splitRow ? parentA : parentB;
      System.arraycopy(source[row], 0, child[row], 0, columns);
    }
    return child;
  }

  private void mutate(boolean[][] pattern, double mutationRate) {
    for (int row = 0; row < pattern.length; row++) {
      for (int column = 0; column < pattern[row].length; column++) {
        if (random.nextDouble() < mutationRate) {
          pattern[row][column] = !pattern[row][column];
        }
      }
    }
  }

  private boolean[][] randomPattern(int rows, int columns) {
    boolean[][] pattern = new boolean[rows][columns];
    double probability = 0.25 + random.nextDouble() * 0.25;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        pattern[row][column] = random.nextDouble() < probability;
      }
    }
    return pattern;
  }

  private int countAlive(boolean[][] pattern) {
    int count = 0;
    for (boolean[] row : pattern) {
      for (boolean cell : row) {
        if (cell) {
          count++;
        }
      }
    }
    return count;
  }

  private boolean[][] advance(boolean[][] pattern) {
    int rows = pattern.length;
    int columns = pattern[0].length;
    boolean[][] next = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        int neighbors = countNeighbors(pattern, row, column);
        if (pattern[row][column]) {
          next[row][column] = neighbors == 2 || neighbors == 3;
        } else {
          next[row][column] = neighbors == 3;
        }
      }
    }
    return next;
  }

  private int countGliders(boolean[][] pattern) {
    int rows = pattern.length;
    int columns = pattern[0].length;
    int count = 0;
    for (int row = 0; row <= rows - 3; row++) {
      for (int column = 0; column <= columns - 3; column++) {
        if (matchesGlider(pattern, row, column)) {
          count++;
        }
      }
    }
    return count;
  }

  private boolean matchesGlider(boolean[][] pattern, int startRow, int startColumn) {
    boolean[][][] shapes = GLIDER_SHAPES;
    for (boolean[][] shape : shapes) {
      boolean match = true;
      for (int row = 0; row < 3 && match; row++) {
        for (int column = 0; column < 3; column++) {
          if (pattern[startRow + row][startColumn + column] != shape[row][column]) {
            match = false;
            break;
          }
        }
      }
      if (match) {
        return true;
      }
    }
    return false;
  }

  private String signatureFor(boolean[][] pattern) {
    int rows = pattern.length;
    int columns = pattern[0].length;
    StringBuilder builder = new StringBuilder(rows * (columns + 1));
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        builder.append(pattern[row][column] ? '1' : '0');
      }
      builder.append('|');
    }
    return builder.toString();
  }

  private int countNeighbors(boolean[][] pattern, int row, int column) {
    int rows = pattern.length;
    int columns = pattern[0].length;
    int count = 0;
    for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
      for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
        if (rowOffset == 0 && columnOffset == 0) {
          continue;
        }
        int neighborRow = row + rowOffset;
        int neighborColumn = column + columnOffset;
        if (neighborRow < 0 || neighborRow >= rows) {
          continue;
        }
        if (neighborColumn < 0 || neighborColumn >= columns) {
          continue;
        }
        if (pattern[neighborRow][neighborColumn]) {
          count++;
        }
      }
    }
    return count;
  }

  private boolean[][] copyPattern(boolean[][] pattern) {
    int rows = pattern.length;
    int columns = pattern[0].length;
    boolean[][] copy = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      System.arraycopy(pattern[row], 0, copy[row], 0, columns);
    }
    return copy;
  }

  private void checkCancelled(CancellationToken token) {
    if (Thread.currentThread().isInterrupted()) {
      throw new CancellationException("Genetic search interrupted.");
    }
    if (token != null && token.isCancelled()) {
      throw new CancellationException("Genetic search cancelled.");
    }
  }

  public enum GeneticObjective {
    HIGH_POPULATION,
    OSCILLATOR,
    GLIDER
  }

  public static final class CancellationToken {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    public void cancel() {
      cancelled.set(true);
    }

    public boolean isCancelled() {
      return cancelled.get();
    }
  }

  private static final class ScoredPattern {
    private final boolean[][] pattern;
    private final double fitness;

    private ScoredPattern(boolean[][] pattern, double fitness) {
      this.pattern = pattern;
      this.fitness = fitness;
    }
  }

  public record GeneticSearchConfig(
      int rows,
      int columns,
      int populationSize,
      int generations,
      int evaluationSteps,
      double mutationRate,
      double crossoverRate,
      GeneticObjective objective) {

    public GeneticSearchConfig {
      if (rows < 1 || columns < 1) {
        throw new IllegalArgumentException("Grid dimensions must be positive.");
      }
      if (populationSize < 2) {
        throw new IllegalArgumentException("Population must be at least 2.");
      }
      if (generations < 1) {
        throw new IllegalArgumentException("Generations must be positive.");
      }
      if (evaluationSteps < 1) {
        throw new IllegalArgumentException("Evaluation steps must be positive.");
      }
      if (mutationRate < 0 || mutationRate > 1) {
        throw new IllegalArgumentException("Mutation rate must be between 0 and 1.");
      }
      if (crossoverRate < 0 || crossoverRate > 1) {
        throw new IllegalArgumentException("Crossover rate must be between 0 and 1.");
      }
      if (objective == null) {
        throw new IllegalArgumentException("Objective must be defined.");
      }
    }
  }

  public record GeneticSearchResult(boolean[][] pattern, double fitness) {

    public GeneticSearchResult {
      if (pattern == null || pattern.length == 0 || pattern[0].length == 0) {
        throw new IllegalArgumentException("Pattern must not be empty.");
      }
    }
  }

  private static final boolean[][][] GLIDER_SHAPES =
      new boolean[][][] {
        new boolean[][] {
          {false, true, false},
          {false, false, true},
          {true, true, true}
        },
        new boolean[][] {
          {true, false, false},
          {true, false, true},
          {true, true, false}
        },
        new boolean[][] {
          {true, true, true},
          {true, false, false},
          {false, true, false}
        },
        new boolean[][] {
          {false, true, true},
          {true, false, true},
          {false, false, true}
        }
      };

  private static final class DaemonThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, "simulation-genetic-worker");
      thread.setDaemon(true);
      return thread;
    }
  }
}
