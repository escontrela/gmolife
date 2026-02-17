# Game of Life

Game of Life es una aplicacion visual para explorar el famoso juego de la vida de Conway. Permite crear, editar y simular patrones de celulas, observar su evolucion y usar una IA integrada para descubrir configuraciones interesantes sin escribir codigo.

## Que puedes hacer
- Simular patrones con controles de Play, Pause y Step para avanzar a tu ritmo.
- Ajustar la velocidad y el zoom para ver el detalle que necesitas.
- Cambiar el tamano del tablero y habilitar el modo toroidal.
- Dibujar y editar celulas directamente en la cuadricula.
- Cargar patrones basicos como Glider o Blinker.
- Guardar y cargar patrones propios, copiar y pegar desde el portapapeles.
- Centrar el patron actual en el tablero.
- Exportar una imagen PNG del tablero.
- Ver contadores de generacion, poblacion, min/max/promedio y TPS.
- Consultar una grafica de poblacion con exportacion a CSV.
- Ejecutar la IA para buscar un patron prometedor, ver su fitness y vista previa.
- Cancelar una busqueda de IA en curso y aplicar el mejor patron encontrado con un clic.

## Controles principales
- Play: inicia la simulacion automatica.
- Pause: detiene la simulacion.
- Step: avanza una generacion.
- Reset: limpia el tablero y reinicia contadores.
- Randomize: genera un tablero inicial aleatorio.
- Centrar: reubica el patron en el centro del tablero.
- Velocidad: ajusta el tiempo entre generaciones.
- Zoom: cambia el tamano de las celdas.
- Guardar/Cargar: guarda patrones en archivo y los recupera despues.
- Copiar/Pegar: lleva el patron al portapapeles o lo importa desde alli.
- Exportar PNG: genera una imagen del tablero actual.
- Exportar CSV: descarga la serie de poblacion mostrada en la grafica.
- IA: define parametros (poblacion, generaciones, mutacion) y ejecuta la busqueda.
- Aplicar IA: carga el patron sugerido por la IA en el tablero.

## Guia rapida de uso (5-8 pasos)
1. Elige un tamano de tablero y ajusta el zoom si lo necesitas.
2. Dibuja algunas celulas o selecciona un patron basico.
3. Pulsa Play para ver la evolucion, o Step para avanzar manualmente.
4. Ajusta la velocidad mientras corre la simulacion.
5. Si encuentras un estado interesante, usa Guardar o Exportar PNG.
6. Si quieres explorar automaticamente, abre la seccion IA y presiona Buscar.
7. Revisa el fitness y la vista previa del resultado de IA.
8. Si te gusta el patron, pulsa Aplicar IA y continua la simulacion.

## Notas
- La aplicacion esta pensada para exploracion visual; no requiere conocimientos tecnicos.
- La funcion de IA busca patrones que mantengan una poblacion interesante segun su fitness.
