# Features

## TASK-000 — Bootstrap del proyecto
Estado: done

## Objetivo
Establecer la base de la aplicacion JavaFX y el esqueleto del motor sin funcionalidad.

## Historia de Usuario
Como usuario, quiero abrir la app y ver una ventana base, para confirmar que la base del proyecto funciona.

## Criterios de Aceptacion
- [x] Proyecto JavaFX arranca con una ventana base sin errores.
- [x] Estructura de paquetes iniciales creada (UI, motor, app).
- [x] App compila y corre sin UI interactiva ni simulacion.

## Fuera de Alcance
- UI interactiva (grid/celdas).
- Simulacion (ticks/reglas).
- Guardar/cargar patrones.
- IA.

## TASK-001 — Cuadricula editable en UI
Estado: done

## Objetivo
Permitir editar manualmente el estado viva/muerta de celulas en una cuadricula visible en JavaFX.

## Historia de Usuario
Como usuario, quiero hacer clic en celdas de la cuadricula para alternar su estado, para diseñar patrones iniciales antes de simular.

## Criterios de Aceptacion
- [x] La ventana principal muestra una cuadricula con celdas visibles.
- [x] Al hacer clic en una celda, alterna entre viva y muerta con cambio visual inmediato.
- [x] El estado de la cuadricula se mantiene en memoria mientras la app esta abierta.

## Fuera de Alcance
- Reglas de simulacion por ticks.
- Controles Play/Pause/Step.
- Guardar o cargar patrones.
- Modulo de IA.

## TASK-002 — Simulacion de un paso
Estado: done

## Objetivo
Agregar un paso unico de simulacion que actualice la cuadricula con las reglas clasicas del Game of Life.

## Historia de Usuario
Como usuario, quiero ejecutar un paso de simulacion, para ver como evoluciona el patron sin iniciar un play continuo.

## Criterios de Aceptacion
- [x] Existe un boton "Step" que aplica un tick a la cuadricula.
- [x] El tick usa las reglas clasicas (nace con 3, sobrevive con 2-3, muere en otros casos).
- [x] La UI refleja el nuevo estado inmediatamente tras presionar Step.

## Fuera de Alcance
- Play/Pause con temporizador.
- Control de velocidad.
- Contadores de generacion/poblacion.
- Guardar/cargar patrones.
- Modulo de IA.

## TASK-003 — Play/Pause con temporizador
Estado: backlog

## Objetivo
Permitir iniciar y pausar la simulacion continua con un temporizador fijo.

## Historia de Usuario
Como usuario, quiero reproducir y pausar la simulacion automaticamente, para observar la evolucion sin presionar Step repetidamente.

## Criterios de Aceptacion
- [ ] Existe un boton "Play" que inicia ticks continuos con un temporizador fijo.
- [ ] Existe un boton "Pause" que detiene los ticks.
- [ ] Mientras esta en Play, la cuadricula se actualiza automaticamente en pantalla.

## Fuera de Alcance
- Control de velocidad.
- Contadores de generacion/poblacion.
- Guardar o cargar patrones.
- Modulo de IA.
