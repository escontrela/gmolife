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
Estado: backlog

## Objetivo
Permitir editar manualmente el estado viva/muerta de celulas en una cuadricula visible en JavaFX.

## Historia de Usuario
Como usuario, quiero hacer clic en celdas de la cuadricula para alternar su estado, para diseñar patrones iniciales antes de simular.

## Criterios de Aceptacion
- [ ] La ventana principal muestra una cuadricula con celdas visibles.
- [ ] Al hacer clic en una celda, alterna entre viva y muerta con cambio visual inmediato.
- [ ] El estado de la cuadricula se mantiene en memoria mientras la app esta abierta.

## Fuera de Alcance
- Reglas de simulacion por ticks.
- Controles Play/Pause/Step.
- Guardar o cargar patrones.
- Modulo de IA.
