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
Estado: done

## Objetivo
Permitir iniciar y pausar la simulacion continua con un temporizador fijo.

## Historia de Usuario
Como usuario, quiero reproducir y pausar la simulacion automaticamente, para observar la evolucion sin presionar Step repetidamente.

## Criterios de Aceptacion
- [x] Existe un boton "Play" que inicia ticks continuos con un temporizador fijo.
- [x] Existe un boton "Pause" que detiene los ticks.
- [x] Mientras esta en Play, la cuadricula se actualiza automaticamente en pantalla.

## Fuera de Alcance
- Control de velocidad.
- Contadores de generacion/poblacion.
- Guardar o cargar patrones.
- Modulo de IA.

## TASK-004 — Contadores de generacion y poblacion
Estado: done

## Objetivo
Mostrar contadores de generacion y poblacion que se actualicen con cada tick.

## Historia de Usuario
Como usuario, quiero ver la generacion actual y la cantidad de celulas vivas, para entender la evolucion del patron.

## Criterios de Aceptacion
- [x] La UI muestra un contador de generacion visible.
- [x] La UI muestra un contador de poblacion visible.
- [x] Ambos contadores se actualizan al usar Step y durante Play.

## Fuera de Alcance
- Control de velocidad.
- Guardar/cargar patrones.
- Modulo de IA.

## TASK-005 — Reset de simulacion
Estado: done

## Objetivo
Permitir reiniciar la simulacion y limpiar la cuadricula con un solo clic.

## Historia de Usuario
Como usuario, quiero reiniciar la simulacion y borrar el tablero, para empezar un patron nuevo rapidamente.

## Criterios de Aceptacion
- [x] Existe un boton "Reset" visible en la UI.
- [x] Al hacer clic en Reset, se detiene el Play si esta activo.
- [x] La cuadricula queda vacia (todas las celdas muertas) y la UI se refresca.
- [x] Los contadores de generacion y poblacion vuelven a 0.

## Fuera de Alcance
- Control de velocidad.
- Guardar/cargar patrones.
- Modulo de IA.
- Zoom o redimension de la cuadricula.

## TASK-006 — Control de velocidad del play
Estado: done

## Objetivo
Permitir ajustar la velocidad de la simulacion continua.

## Historia de Usuario
Como usuario, quiero cambiar la velocidad del Play, para observar la evolucion a diferentes ritmos.

## Criterios de Aceptacion
- [x] Existe un control de velocidad visible (slider o selector) en la UI.
- [x] Al modificar la velocidad durante Play, el temporizador ajusta el intervalo sin reiniciar la simulacion.
- [x] La velocidad seleccionada se aplica tambien al reanudar Play despues de Pause.

## Fuera de Alcance
- Zoom o redimension de la cuadricula.
- Guardar/cargar patrones.
- Modulo de IA.
- Graficos de evolucion.

## TASK-007 — Randomize inicial evolutivo
Estado: done

## Objetivo
Agregar un boton Randomize que genere un patron inicial aleatorio con alta probabilidad de evolucion visible.

## Historia de Usuario
Como usuario, quiero randomizar la cuadricula con un patron que evolucione al presionar Play, para explorar comportamientos interesantes sin pintar celulas manualmente.

## Criterios de Aceptacion
- [x] Existe un boton "Randomize" visible en la UI principal.
- [x] Al presionar Randomize, la cuadricula se rellena automaticamente con una distribucion aleatoria de celulas vivas/muertas.
- [x] El algoritmo evita estados triviales (tablero vacio o totalmente lleno) y reintenta hasta obtener un patron valido.
- [x] Al ejecutar 5 pasos de simulacion tras Randomize, se observa al menos un cambio de estado en la cuadricula.

## Fuera de Alcance
- Guardar/cargar semillas aleatorias.
- Configuracion avanzada de probabilidad por el usuario.
- Modulo de IA para optimizacion de patrones.
- Graficos de evolucion.

## TASK-008 — Grafica de poblacion a la derecha
Estado: done

## Objetivo
Mostrar una grafica simple de evolucion de poblacion a la derecha de la cuadricula.

## Historia de Usuario
Como usuario, quiero ver una grafica de la poblacion por generacion mientras simulo, para entender tendencias del patron mas alla del estado visual de la rejilla.

## Criterios de Aceptacion
- [x] La UI muestra un panel a la derecha de la cuadricula con una grafica de linea (X=generacion, Y=poblacion).
- [x] La grafica se actualiza al usar Step y durante Play sin bloquear la interfaz.
- [x] Al pulsar Reset, la serie de la grafica se reinicia al estado inicial.
- [x] La grafica mantiene como minimo los ultimos 100 puntos para evitar crecimiento infinito en memoria.

## Fuera de Alcance
- Multiples series (nacimientos/muertes por separado).
- Exportar la grafica a imagen o CSV.
- Analiticas avanzadas o predicciones.
- Integracion con modulo de IA.

## TASK-009 — Zoom de cuadricula
Estado: done

## Objetivo
Permitir acercar o alejar la cuadricula para mejorar la visualizacion.

## Historia de Usuario
Como usuario, quiero hacer zoom en la cuadricula, para ver mas detalle o mas area del tablero segun lo necesite.

## Criterios de Aceptacion
- [x] Existe un control de zoom (botones +/- o slider) visible en la UI.
- [x] Al ajustar el zoom, las celdas cambian de tamanio sin distorsionar la cuadricula.
- [x] El nivel de zoom se mantiene durante la sesion, incluso al hacer Reset.

## Fuera de Alcance
- Guardar el zoom entre sesiones.
- Pan o desplazamiento con drag.
- Auto-ajuste por tamanio de ventana.
