# saboteur-unlu-poo

Este proyecto es una implementación del juego de mesa Saboteur en Java para la materia Programación orientada a objetos de la Universidad Nacional de Luján.

Reglas del juego:

TO DO: REDACTAR REGLAS.

Lista de TO-DOs:

- [ ] Agregar posibilidad de elegir el nombre del jugador en lugar de generar uno en el servidor
- [ ] Crear un lobby previo a la partida hasta que todos los jugadores estén listos
- [ ] Agregar imágenes para las cartas, especialmente las de túneles. La idea sería que cada carta se divida en nueve partes: las esquinas son fijas (no hay entradas o salidas por las esquinas) y las secciones del medio en cada uno de los cuatro lados de cada carta se va a tomar dinámicamente dependiendo de si la carta tiene una entrada/salida ahí o no. La sección central de cada carta puede ser un bloque vacío o de piedra dependiendo de si la carta tiene algo en el medio o no
- [ ] Como la cantidad de cartas en el juego es finita, se podría listar cada carta y sus características en un archivo. Tanto el servidor como cada cliente podría leerlo para generar los objetos necesarios en memoria y luego enviar eventos utilizando solo el id de cada carta
- [ ] Agregar validaciones para los movimientos. Determinar si conviene que solo estén del lado del server, solo en el cliente o en ambos
- [ ] Implementar breadth-first-search para validar que las cartas de túnel que los jugadores intentan colocar en la grilla sean un movimiento válido
