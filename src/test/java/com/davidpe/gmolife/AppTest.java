package com.davidpe.gmolife;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.davidpe.gmolife.engine.SimulationEngine;
import org.junit.jupiter.api.Test;

class AppTest {

  @Test
  void simulationEngineIsNotStartedByDefault() {
    SimulationEngine engine = new SimulationEngine();
    assertFalse(engine.isStarted());
  }
}
