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

## TASK-010 — Guardar patron a archivo
Estado: done

## Objetivo
Permitir guardar el patron actual de la cuadricula en un archivo local.

## Historia de Usuario
Como usuario, quiero guardar mi patron en un archivo, para poder reutilizarlo mas tarde.

## Criterios de Aceptacion
- [x] Existe un boton "Save" o "Guardar" visible en la UI.
- [x] Al presionar Save, se abre un selector de archivo para elegir ubicacion y nombre.
- [x] El archivo se guarda en un formato de texto simple con dimensiones y celdas (0/1).
- [x] Tras guardar, se muestra una confirmacion en la UI (toast, label o dialogo).

## Fuera de Alcance
- Formatos estandar como RLE.
- Guardado automatico.
- Versionado de patrones.

## TASK-011 — Cargar patron desde archivo
Estado: done

## Objetivo
Permitir cargar un patron desde un archivo local y aplicarlo a la cuadricula.

## Historia de Usuario
Como usuario, quiero abrir un patron guardado, para continuar trabajando en el sin recrearlo.

## Criterios de Aceptacion
- [x] Existe un boton "Load" o "Abrir" visible en la UI.
- [x] Al presionar Load, se abre un selector de archivo para elegir un archivo de patron.
- [x] El archivo usa el mismo formato de texto simple definido en TASK-010.
- [x] Al cargar, la cuadricula se actualiza y los contadores se recalculan (generacion=0, poblacion acorde).
- [x] Si el archivo es invalido, se muestra un mensaje de error claro.

## Fuera de Alcance
- Soporte para multiples formatos.
- Historial de archivos recientes.
- Validacion avanzada del contenido.

## TASK-012 — Biblioteca de patrones basicos
Estado: done

## Objetivo
Ofrecer una lista de patrones predefinidos para cargar rapidamente en la cuadricula.

## Historia de Usuario
Como usuario, quiero seleccionar patrones clasicos, para explorar comportamientos sin dibujarlos manualmente.

## Criterios de Aceptacion
- [x] Existe un menu o selector de "Patrones" visible en la UI.
- [x] Incluye al menos: Glider, Blinker, Toad y Beacon.
- [x] Al seleccionar un patron, la cuadricula se limpia y el patron se inserta centrado.
- [x] Los contadores se reinician y reflejan el patron cargado.

## Fuera de Alcance
- Editor de patrones.
- Importar patrones desde internet.
- Guardar favoritos.

## TASK-013 — Atajos de teclado principales
Estado: done

## Objetivo
Permitir controlar la simulacion con atajos de teclado basicos.

## Historia de Usuario
Como usuario, quiero usar atajos de teclado, para operar la simulacion mas rapido.

## Criterios de Aceptacion
- [x] Barra espaciadora alterna Play/Pause.
- [x] Tecla "N" ejecuta un Step.
- [x] Tecla "R" ejecuta Reset.
- [x] Los atajos funcionan cuando la ventana principal tiene foco.

## Fuera de Alcance
- Atajos configurables.
- Soporte para combos avanzados.
- Atajos globales del sistema.

## TASK-014 — Algoritmo genetico base en SimulationEngine
Estado: done

## Objetivo
Agregar un algoritmo genetico minimo que busque un patron inicial con mejor fitness usando SimulationEngine.

## Historia de Usuario
Como usuario, quiero que el sistema proponga automaticamente patrones iniciales prometedores, para explorar configuraciones interesantes sin prueba y error manual.

## Criterios de Aceptacion
- [x] Se implementa un componente genetico en Java que genere una poblacion inicial de patrones binarios.
- [x] Cada individuo se evalua ejecutando N generaciones con SimulationEngine y calculando fitness (poblacion media durante N ticks).
- [x] Se aplican al menos operadores de seleccion, cruce y mutacion durante varias iteraciones.
- [x] El proceso devuelve el mejor patron encontrado y su fitness numerico.
- [x] La ejecucion es no bloqueante para UI (servicio en background o tarea asincrona).

## Fuera de Alcance
- Ajuste avanzado de hiperparametros desde UI.
- Multiples objetivos de fitness.
- Visualizacion grafica detallada del progreso genetico.

## TASK-016 — Panel IA: ejecutar busqueda genetica
Estado: backlog

## Objetivo
Agregar un panel de IA en la UI para lanzar la busqueda genetica y mostrar el mejor fitness encontrado.

## Historia de Usuario
Como usuario, quiero iniciar la busqueda de patrones desde la interfaz y ver el fitness resultante, para evaluar rapidamente si la IA encontro algo interesante.

## Criterios de Aceptacion
- [ ] Existe una seccion "IA" en la UI con un boton "Buscar" o "Run".
- [ ] Al ejecutar, la busqueda genetica corre en background y la UI sigue respondiendo.
- [ ] Al finalizar, se muestra el mejor fitness en la interfaz.
- [ ] La ejecucion no bloquea Play/Pause/Step ni el render de la cuadricula.

## Fuera de Alcance
- Aplicar el patron a la cuadricula.
- Cancelacion de la busqueda.
- Configuracion avanzada de parametros.
- Visualizacion grafica del progreso.

## TASK-017 — Aplicar patron IA a la cuadricula
Estado: backlog

## Objetivo
Permitir aplicar el mejor patron encontrado por la IA directamente a la cuadricula actual.

## Historia de Usuario
Como usuario, quiero aplicar el resultado de la IA con un clic, para probar el patron sugerido sin copiarlo manualmente.

## Criterios de Aceptacion
- [ ] Existe un boton "Aplicar IA" visible en la seccion de IA.
- [ ] El boton solo se habilita cuando existe un resultado valido de IA.
- [ ] Al aplicar, la cuadricula se carga centrada con el patron de IA y la generacion vuelve a 0.
- [ ] La poblacion y grafica se actualizan acorde al nuevo estado.

## Fuera de Alcance
- Guardar automaticamente el patron a archivo.
- Comparacion visual entre patron actual y el de IA.
- Edicion avanzada del patron sugerido.

## TASK-018 — Cancelar busqueda IA en curso
Estado: backlog

## Objetivo
Permitir cancelar una busqueda genetica en progreso desde la UI.

## Historia de Usuario
Como usuario, quiero cancelar la ejecucion de IA cuando tarda demasiado, para retomar el control sin cerrar la app.

## Criterios de Aceptacion
- [ ] Existe un boton "Cancelar IA" visible durante una ejecucion activa.
- [ ] Al cancelar, el proceso de busqueda se detiene sin bloquear la UI.
- [ ] La interfaz refleja el estado "cancelado" y permite volver a ejecutar.
- [ ] No se actualiza el resultado final si la ejecucion fue cancelada.

## Fuera de Alcance
- Reintentar automaticamente tras cancelacion.
- Guardar resultados parciales.
- Cancelacion de otras tareas de la app.

## TASK-019 — Vista previa del patron IA
Estado: backlog

## Objetivo
Mostrar una vista previa en miniatura del mejor patron encontrado por la IA.

## Historia de Usuario
Como usuario, quiero ver una vista previa del patron sugerido sin aplicarlo, para decidir si me interesa probarlo.

## Criterios de Aceptacion
- [ ] La seccion de IA incluye una mini-cuadricula o canvas de vista previa.
- [ ] La vista previa se actualiza al finalizar una ejecucion de IA exitosa.
- [ ] Si se ejecuta Reset, la vista previa se limpia.
- [ ] La vista previa no bloquea la interaccion con la cuadricula principal.

## Fuera de Alcance
- Zoom o pan dentro de la vista previa.
- Comparacion lado a lado con el patron actual.
- Exportar la vista previa como imagen.

## TASK-015 — Layout responsive resizable y maximizable
Estado: done

## Objetivo
Hacer que la UI sea redimensionable y maximizable manteniendo todos los controles visibles y proporcionales.

## Historia de Usuario
Como usuario, quiero redimensionar o maximizar la ventana sin perder controles ni distorsionar la interfaz, para usar la aplicacion comodamente en distintos tamanios de pantalla.

## Criterios de Aceptacion
- [x] La ventana principal puede redimensionarse y maximizarse sin recortes de controles principales.
- [x] La cuadricula, grafica y paneles se reacomodan proporcionalmente al cambiar el tamanio.
- [x] Los botones (Play/Pause/Step/Reset/Randomize) y controles clave permanecen visibles y operables en ventana pequena y maximizada.
- [x] Se definen tamanios minimos razonables para evitar colapsos de layout.
- [x] El comportamiento de resize no degrada la interaccion (click en celdas, slider de velocidad y acciones principales).

## Fuera de Alcance
- Rediseño visual completo de estilos.
- Modo multi-ventana.
- Configuracion de layouts por usuario.

## TASK-016 — Panel IA: ejecutar busqueda genetica
Estado: done

## Objetivo
Agregar un panel de IA en la UI para lanzar la busqueda genetica y mostrar el mejor fitness encontrado.

## Historia de Usuario
Como usuario, quiero iniciar la busqueda de patrones desde la interfaz y ver el fitness resultante, para evaluar rapidamente si la IA encontro algo interesante.

## Criterios de Aceptacion
- [x] Existe una seccion "IA" en la UI con un boton "Buscar" o "Run".
- [x] Al ejecutar, la busqueda genetica corre en background y la UI sigue respondiendo.
- [x] Al finalizar, se muestra el mejor fitness en la interfaz.
- [x] La ejecucion no bloquea Play/Pause/Step ni el render de la cuadricula.

## Fuera de Alcance
- Aplicar el patron a la cuadricula.
- Cancelacion de la busqueda.
- Configuracion avanzada de parametros.
- Visualizacion grafica del progreso.

## TASK-017 — Aplicar patron IA a la cuadricula
Estado: done

## Objetivo
Permitir aplicar el mejor patron encontrado por la IA directamente a la cuadricula actual.

## Historia de Usuario
Como usuario, quiero aplicar el resultado de la IA con un clic, para probar el patron sugerido sin copiarlo manualmente.

## Criterios de Aceptacion
- [x] Existe un boton "Aplicar IA" visible en la seccion de IA.
- [x] El boton solo se habilita cuando existe un resultado valido de IA.
- [x] Al aplicar, la cuadricula se carga centrada con el patron de IA y la generacion vuelve a 0.
- [x] La poblacion y grafica se actualizan acorde al nuevo estado.

## Fuera de Alcance
- Guardar automaticamente el patron a archivo.
- Comparacion visual entre patron actual y el de IA.
- Edicion avanzada del patron sugerido.

## TASK-018 — Cancelar busqueda IA en curso
Estado: done

## Objetivo
Permitir cancelar una busqueda genetica en progreso desde la UI.

## Historia de Usuario
Como usuario, quiero cancelar la ejecucion de IA cuando tarda demasiado, para retomar el control sin cerrar la app.

## Criterios de Aceptacion
- [x] Existe un boton "Cancelar IA" visible durante una ejecucion activa.
- [x] Al cancelar, el proceso de busqueda se detiene sin bloquear la UI.
- [x] La interfaz refleja el estado "cancelado" y permite volver a ejecutar.
- [x] No se actualiza el resultado final si la ejecucion fue cancelada.

## Fuera de Alcance
- Reintentar automaticamente tras cancelacion.
- Guardar resultados parciales.
- Cancelacion de otras tareas de la app.

## TASK-019 — Vista previa del patron IA
Estado: done

## Objetivo
Mostrar una vista previa en miniatura del mejor patron encontrado por la IA.

## Historia de Usuario
Como usuario, quiero ver una vista previa del patron sugerido sin aplicarlo, para decidir si me interesa probarlo.

## Criterios de Aceptacion
- [x] La seccion de IA incluye una mini-cuadricula o canvas de vista previa.
- [x] La vista previa se actualiza al finalizar una ejecucion de IA exitosa.
- [x] Si se ejecuta Reset, la vista previa se limpia.
- [x] La vista previa no bloquea la interaccion con la cuadricula principal.

## Fuera de Alcance
- Zoom o pan dentro de la vista previa.
- Comparacion lado a lado con el patron actual.
- Exportar la vista previa como imagen.

## TASK-020 — Parametros basicos de IA configurables
Estado: done

## Objetivo
Permitir ajustar parametros basicos de la busqueda genetica desde la UI.

## Historia de Usuario
Como usuario, quiero configurar parametros basicos de la IA, para controlar el esfuerzo de busqueda segun mi necesidad.

## Criterios de Aceptacion
- [x] La seccion de IA incluye campos para poblacion, generaciones y tasa de mutacion con valores por defecto.
- [x] Al ejecutar la IA, la busqueda usa los valores configurados por el usuario.
- [x] Los valores invalidos se corrigen o muestran error claro sin romper la UI.

## Fuera de Alcance
- Presets avanzados o guardado persistente de configuracion.
- Edicion de parametros avanzados (elitismo, cruce, etc.).
- Multiples objetivos de fitness.

## TASK-021 — Progreso IA en tiempo real
Estado: done

## Objetivo
Mostrar el avance de la busqueda genetica durante la ejecucion.

## Historia de Usuario
Como usuario, quiero ver el progreso actual de la IA mientras corre, para decidir si espero o cancelo.

## Criterios de Aceptacion
- [x] La seccion de IA muestra la iteracion actual y el mejor fitness parcial durante la ejecucion.
- [x] Los valores se actualizan periodicamente sin bloquear la UI.
- [x] Al finalizar o cancelar, el estado se actualiza a completado o cancelado.

## Fuera de Alcance
- Grafica detallada del progreso.
- Exportar historico de fitness.
- Notificaciones del sistema.

## TASK-022 — Grafica de nacimientos y muertes
Estado: done

## Objetivo
Agregar series de nacimientos y muertes por generacion en la grafica de evolucion.

## Historia de Usuario
Como usuario, quiero ver nacimientos y muertes por generacion, para entender mejor la dinamica del patron.

## Criterios de Aceptacion
- [x] La grafica incluye dos series adicionales: nacimientos y muertes por generacion.
- [x] Las series se actualizan al usar Step y durante Play.
- [x] Al pulsar Reset, las series se reinician.
- [x] Se mantienen como minimo los ultimos 100 puntos por serie.

## Fuera de Alcance
- Estadisticas acumuladas o promedios.
- Exportar datos a CSV.
- Anotaciones en la grafica.

## TASK-023 — Selector de tamano de cuadricula
Estado: done

## Objetivo
Permitir elegir el tamano de la cuadricula desde la UI.

## Historia de Usuario
Como usuario, quiero seleccionar un tamano de tablero predefinido, para adaptar el espacio de simulacion a mi pantalla.

## Criterios de Aceptacion
- [x] Existe un selector con tamanos predefinidos (por ejemplo 50x30, 100x60, 150x90).
- [x] Al cambiar el tamano, la cuadricula se reinicia vacia y los contadores vuelven a 0.
- [x] La grafica se reinicia al aplicar un nuevo tamano.
- [x] El tamano seleccionado se mantiene durante la sesion.

## Fuera de Alcance
- Ingreso de tamanos personalizados.
- Persistencia entre sesiones.
- Reescalado automatico del contenido existente.

## TASK-024 — Modo toroidal (wrap-around)
Estado: done

## Objetivo
Permitir alternar un modo toroidal para el calculo de vecinos en la simulacion.

## Historia de Usuario
Como usuario, quiero activar el modo toroidal, para que los bordes se conecten y observar patrones continuos.

## Criterios de Aceptacion
- [x] Existe un toggle "Toroidal" en la UI de simulacion.
- [x] Cuando esta activo, la simulacion calcula vecinos con wrap-around en los bordes.
- [x] El cambio de modo se aplica al siguiente tick sin reiniciar la cuadricula.
- [x] El modo por defecto es no toroidal.

## Fuera de Alcance
- Multiples modos de frontera.
- Persistencia del modo entre sesiones.
- Visualizacion especial de bordes.

## TASK-025 — Indicador de IA en ejecucion
Estado: done

## Objetivo
Mostrar claramente cuando la IA esta ejecutandose para evitar acciones duplicadas.

## Historia de Usuario
Como usuario, quiero un indicador visual de IA en curso, para saber que la busqueda esta activa.

## Criterios de Aceptacion
- [x] Al iniciar la IA, aparece un indicador visible con texto "IA en ejecucion".
- [x] El indicador desaparece al terminar o cancelar la ejecucion.
- [x] Mientras la IA corre, el boton Run/Buscar queda deshabilitado para evitar ejecuciones simultaneas.

## Fuera de Alcance
- Barra de progreso detallada.
- Notificaciones del sistema.
- Multiples ejecuciones concurrentes.

## TASK-026 — Toggle de lineas de cuadricula
Estado: done

## Objetivo
Permitir mostrar u ocultar las lineas de la cuadricula para mejorar la visualizacion.

## Historia de Usuario
Como usuario, quiero poder ocultar las lineas de la cuadricula, para ver el patron con menos ruido visual.

## Criterios de Aceptacion
- [x] Existe un toggle o checkbox "Lineas de cuadricula" en la UI.
- [x] Al desactivar, las lineas desaparecen sin afectar estados de celdas ni interaccion.
- [x] Al activar, las lineas vuelven a mostrarse inmediatamente.
- [x] El estado del toggle se mantiene durante la sesion y no cambia con Reset.

## Fuera de Alcance
- Persistencia entre sesiones.
- Cambios de estilo avanzados.
- Temas o skins.

## TASK-027 — Indicador de ticks por segundo
Estado: done

## Objetivo
Mostrar la velocidad real de simulacion en ticks por segundo.

## Historia de Usuario
Como usuario, quiero ver los ticks por segundo actuales, para ajustar la velocidad de forma informada.

## Criterios de Aceptacion
- [x] La UI muestra un label "TPS" con el valor actual.
- [x] Durante Play, el valor se actualiza al menos 1 vez por segundo.
- [x] En Pause, el valor muestra 0 o "--".
- [x] El calculo no bloquea la UI ni degrada el render.

## Fuera de Alcance
- Grafica historica de TPS.
- Calibracion automatica de velocidad.
- Exportacion de metricas.

## TASK-028 — Exportar grafica de poblacion a CSV
Estado: done

## Objetivo
Permitir exportar la serie de poblacion a un archivo CSV.

## Historia de Usuario
Como usuario, quiero exportar la grafica de poblacion a CSV, para analizarla fuera de la app.

## Criterios de Aceptacion
- [x] Existe un boton "Exportar CSV" en el panel de grafica.
- [x] Al pulsarlo, se abre un selector de archivo para guardar.
- [x] El CSV incluye columnas "generacion" y "poblacion" con los puntos disponibles (max 100).
- [x] Se muestra confirmacion de guardado exitoso o mensaje de error.

## Fuera de Alcance
- Exportar otras series (nacimientos/muertes).
- Formatos adicionales (JSON, PNG).
- Exportacion automatica.

## TASK-029 — Estadisticas de poblacion (min/max/promedio)
Estado: done

## Objetivo
Mostrar estadisticas basicas de poblacion durante la simulacion.

## Historia de Usuario
Como usuario, quiero ver min/max/promedio de poblacion, para entender mejor la evolucion del patron.

## Criterios de Aceptacion
- [x] La UI muestra tres labels: min, max y promedio de poblacion.
- [x] Los valores se recalculan al usar Step y durante Play.
- [x] Al pulsar Reset, las estadisticas vuelven a 0.
- [x] El calculo no bloquea la UI.

## Fuera de Alcance
- Estadisticas por ventana de tiempo.
- Exportar estadisticas.
- Comparativas entre ejecuciones.

## TASK-031 — Confirmacion de reset con tablero no vacio
Estado: done

## Objetivo
Evitar resets accidentales cuando hay celdas vivas en el tablero.

## Historia de Usuario
Como usuario, quiero confirmar el reset si tengo un patron en pantalla, para no perder trabajo por error.

## Criterios de Aceptacion
- [x] Si el tablero tiene al menos una celda viva, Reset muestra un dialogo de confirmacion.
- [x] Si el usuario confirma, se ejecuta Reset como hoy.
- [x] Si el usuario cancela, el tablero y contadores permanecen intactos.
- [x] Si el tablero esta vacio, Reset se ejecuta sin dialogo.

## Fuera de Alcance
- Preferencias persistentes de confirmacion.
- Confirmaciones para otras acciones (Load, Randomize).

## TASK-032 — Exportar imagen PNG del tablero
Estado: done

## Objetivo
Permitir guardar una imagen PNG del estado actual de la cuadricula.

## Historia de Usuario
Como usuario, quiero exportar una imagen del patron actual, para compartirlo o documentarlo.

## Criterios de Aceptacion
- [x] Existe un boton "Exportar PNG" visible en la UI.
- [x] Al presionarlo, se abre un selector para guardar el archivo.
- [x] El PNG contiene solo la cuadricula (sin controles) con los colores actuales.
- [x] Se muestra confirmacion de guardado exitoso o mensaje de error.

## Fuera de Alcance
- Exportar animaciones o multiples frames.
- Formatos alternativos (SVG, JPG).
- Exportacion automatica.

## TASK-033 — Copiar patron al portapapeles
Estado: done

## Objetivo
Permitir copiar el patron actual en formato de texto al portapapeles.

## Historia de Usuario
Como usuario, quiero copiar el patron actual al portapapeles, para compartirlo rapidamente sin guardar un archivo.

## Criterios de Aceptacion
- [x] Existe un boton "Copiar patron" en la UI.
- [x] Al presionarlo, se copia el texto en el mismo formato que PatternIO.
- [x] Se muestra una confirmacion visual de copia exitosa.
- [x] Si ocurre un error, se muestra un mensaje claro.

## Fuera de Alcance
- Formatos alternativos (RLE, JSON).
- Copiar solo la seleccion.
- Historial de copiados.

## TASK-034 — Pegar patron desde portapapeles
Estado: done

## Objetivo
Permitir cargar un patron desde el portapapeles usando el formato de PatternIO.

## Historia de Usuario
Como usuario, quiero pegar un patron desde el portapapeles, para cargar rapidamente patrones compartidos.

## Criterios de Aceptacion
- [x] Existe un boton "Pegar patron" en la UI.
- [x] Si el texto es valido, la cuadricula se carga centrada y la generacion vuelve a 0.
- [x] Si el texto es invalido, se muestra un mensaje de error claro.
- [x] La poblacion y grafica se recalculan al aplicar el patron.

## Fuera de Alcance
- Auto-deteccion de formatos alternativos.
- Persistencia del portapapeles.
- Historial de pegados.

## TASK-035 — Tests unitarios para GridState y PatternIO
Estado: done

## Objetivo
Agregar pruebas unitarias basicas para la logica de tablero y lectura/escritura de patrones.

## Historia de Usuario
Como equipo, quiero tests de GridState y PatternIO, para prevenir regresiones en reglas y parsing.

## Criterios de Aceptacion
- [x] Tests cubren reglas de avance en GridState (nace, sobrevive, muere).
- [x] Tests cubren modo toroidal en bordes.
- [x] Tests cubren serialize/parse de PatternIO con casos validos e invalidos.
- [x] La suite pasa con `mvn test`.

## Fuera de Alcance
- Tests de UI JavaFX.
- Benchmarks de rendimiento.
- Coverage exhaustivo de IA.

## TASK-036 — Indicador de estado Play/Pause
Estado: done

## Objetivo
Mostrar claramente si la simulacion esta en Play o Pause.

## Historia de Usuario
Como usuario, quiero ver el estado actual de la simulacion, para saber si esta corriendo o detenida.

## Criterios de Aceptacion
- [x] La UI muestra un label o badge con estado "Play" o "Pause".
- [x] El estado cambia inmediatamente al pulsar Play o Pause.
- [x] Al iniciar la app, el estado inicial es "Pause".

## Fuera de Alcance
- Animaciones avanzadas del indicador.
- Persistencia del estado entre sesiones.

## TASK-038 — Confirmacion al cargar patron con tablero no vacio
Estado: done

## Objetivo
Evitar perder trabajo al cargar un patron cuando hay celdas vivas.

## Historia de Usuario
Como usuario, quiero confirmar la carga si tengo un patron en pantalla, para no sobrescribirlo por error.

## Criterios de Aceptacion
- [x] Si el tablero tiene al menos una celda viva, Load muestra un dialogo de confirmacion.
- [x] Si el usuario confirma, se carga el patron como hoy.
- [x] Si el usuario cancela, la cuadricula y contadores permanecen intactos.
- [x] Si el tablero esta vacio, Load se ejecuta sin dialogo.

## Fuera de Alcance
- Confirmaciones para Save o Export.
- Preferencias persistentes de confirmacion.

## TASK-037 — Auto-pause al editar celdas
Estado: done

## Objetivo
Pausar la simulacion cuando el usuario edita la cuadricula durante Play.

## Historia de Usuario
Como usuario, quiero que la simulacion se pause si edito celdas, para evitar resultados confusos mientras la ejecucion esta activa.

## Criterios de Aceptacion
- [x] Si la simulacion esta en Play y el usuario hace click o arrastra para editar, se detiene el temporizador.
- [x] La UI refleja el cambio a estado Pause inmediatamente.
- [x] Si ya estaba en Pause, editar no cambia el estado.

## Fuera de Alcance
- Opcion para desactivar este comportamiento.
- Edicion durante Play sin pausa.

## TASK-040 — Barra de estado para mensajes de acciones
Estado: done

## Objetivo
Mostrar mensajes breves de acciones (guardar, cargar, copiar, exportar) en una barra de estado.

## Historia de Usuario
Como usuario, quiero ver confirmaciones y errores en una barra de estado, para entender rapidamente el resultado de una accion.

## Criterios de Aceptacion
- [x] La UI incluye una barra de estado visible en la ventana principal.
- [x] Al guardar, cargar, copiar o exportar, se muestra un mensaje de exito o error.
- [x] Los mensajes se reemplazan por el mas reciente y no bloquean la UI.

## Fuera de Alcance
- Historial de mensajes.
- Notificaciones del sistema.
- Persistencia del ultimo mensaje.

## TASK-039 — Atajos de teclado para Save/Load
Estado: done

## Objetivo
Permitir usar atajos de teclado para guardar y cargar patrones.

## Historia de Usuario
Como usuario, quiero usar atajos de teclado para Save y Load, para trabajar mas rapido.

## Criterios de Aceptacion
- [x] Ctrl/Cmd+S ejecuta Save cuando la ventana principal tiene foco.
- [x] Ctrl/Cmd+O ejecuta Load cuando la ventana principal tiene foco.
- [x] Los atajos no rompen el uso de menus o botones existentes.

## Fuera de Alcance
- Atajos configurables.
- Atajos para exportar PNG o CSV.

## TASK-041 — Coordenadas del cursor en la cuadricula
Estado: done

## Objetivo
Mostrar la fila y columna de la celda bajo el cursor en tiempo real.

## Historia de Usuario
Como usuario, quiero ver las coordenadas de la celda bajo el mouse, para ubicar patrones con precision.

## Criterios de Aceptacion
- [x] La UI muestra un label con coordenadas (fila, columna).
- [x] Al mover el mouse sobre la cuadricula, las coordenadas se actualizan en tiempo real.
- [x] Al salir de la cuadricula, el label muestra "N/A".
- [x] El calculo respeta zoom y desplazamiento actuales.

## Fuera de Alcance
- Copiar coordenadas al portapapeles.
- Ir a una coordenada especifica.
- Mostrar coordenadas absolutas del canvas.

## TASK-042 — Confirmacion al salir con tablero no vacio
Estado: done

## Objetivo
Evitar perdida de trabajo al cerrar la app con celdas vivas.

## Historia de Usuario
Como usuario, quiero confirmar la salida si tengo un patron en pantalla, para no perderlo por error.

## Criterios de Aceptacion
- [x] Si hay al menos una celda viva, al cerrar la ventana se muestra un dialogo de confirmacion.
- [x] Si el usuario confirma, la app se cierra normalmente.
- [x] Si el usuario cancela, la app permanece abierta sin cambios.
- [x] Si el tablero esta vacio, la app se cierra sin dialogo.

## Fuera de Alcance
- Guardado automatico al salir.
- Preferencias persistentes de confirmacion.
- Confirmaciones para otras acciones (Load, Reset).

## TASK-043 — Boton para centrar patron
Estado: done

## Objetivo
Agregar una accion que centre el patron actual dentro de la cuadricula.

## Historia de Usuario
Como usuario, quiero centrar el patron con un clic, para mantenerlo visible y ordenado en pantalla.

## Criterios de Aceptacion
- [x] Existe un boton "Centrar" visible en la UI.
- [x] Al pulsarlo, el patron vivo se traslada para quedar centrado en la cuadricula.
- [x] La poblacion no cambia tras centrar.
- [x] La UI se actualiza inmediatamente y los contadores se mantienen.

## Fuera de Alcance
- Cambiar tamano de la cuadricula.
- Rotar o espejar el patron.
- Centrado automatico continuo.

## TASK-044 — Dialogo de ayuda con atajos
Estado: done

## Objetivo
Mostrar una ayuda rapida con los atajos de teclado disponibles.

## Historia de Usuario
Como usuario, quiero ver una lista de atajos en pantalla, para aprender a usar la app mas rapido.

## Criterios de Aceptacion
- [x] Existe un boton o menu "Ayuda" visible en la UI.
- [x] Al abrirlo, se muestra un dialogo con los atajos actuales (Play/Pause, Step, Reset, Save, Load).
- [x] El dialogo puede cerrarse sin afectar la simulacion.
- [x] La informacion coincide con los atajos implementados.

## Fuera de Alcance
- Buscador de atajos.
- Atajos configurables desde la UI.
- Tutorial interactivo.
