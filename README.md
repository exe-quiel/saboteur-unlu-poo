# saboteur-unlu-poo

Este proyecto es una implementación del juego de mesa Saboteur en Java para la materia Programación orientada a objetos de la Universidad Nacional de Luján.

Saboteur es un juego de mesa de 3 a 10 jugadores en el que cada jugador tiene un rol secreto: buscador o saboteador: Los buscadores deben lograr descubrir dónde está el oro escondido en una de tres posiciones posibles, llamadas cartas de destino, y los saboteadores deben evitarlo a toda costa.

Para ello, deben usar cartas que podrán colocar sobre una grilla, ya sea para cavar túneles que conecten el inicio del túnel con las cartas de destino. Cuando se genere un camino entre la carta de inicio y alguna de destino, esta última se debe dar vuelta para descubrir si es una piedra o si es el oro al que los buscadores deben llegar. Si es una piedra, el juego continúa.

También pueden jugar cartas de acción que permiten derrumbar partes del túnel, develar una carta de destino a elección o incluso romper (e incluso reparar) las herramientas de los otros jugadores.

La ronda finaliza cuando los buscadores llegan al oro o cuando se acaban las cartas de todos los jugadores y ya no quedan cartas del mazo para levantar. En este último caso, ganan los saboteadores.

El equipo ganador debe repartirse pepitas de oro cada vez que gana y, luego de tres rondas, el que más pepitas tiene, gana la partida.

Esta implementación del juego es limitada y no está terminada. Por ejemplo, una de las limitaciones es que, en el juego real, la grilla se puede extender infinitamente (limitada, obviamente, por la cantidad de cartas). En esta implementación, eso no es posible por cuestión de tiempo.

El proyecto consiste en una interfaz gráfica, cuyo UML es el siguiente:

![Saboteur-UML-UI](https://user-images.githubusercontent.com/20674466/203629918-ff651e91-c65a-4134-be8e-04da5d81c8fd.png)

El código se puede ver [acá](https://github.com/exe-quiel/saboteur-unlu-poo/tree/master/src/ar/edu/unlu/poo/saboteur/vista). Esta interfaz representa a cada uno de los clientes, es decir, jugadores. Se comunica a través de un [controlador](https://github.com/exe-quiel/saboteur-unlu-poo/blob/master/src/ar/edu/unlu/poo/saboteur/controlador/ControladorJuego.java) con el modelo del [juego](https://github.com/exe-quiel/saboteur-unlu-poo/blob/master/src/ar/edu/unlu/poo/saboteur/modelo/impl/Juego.java), que se encarga de mantener el estado de la partida. Este es el UML del modelo:

![Saboteur-UML-Modelo](https://user-images.githubusercontent.com/20674466/203631200-bd7a9564-d2ea-41db-89a4-a612ab1b6450.png)

La interfaz genera eventos (por ejemplo, cuando un jugador coloca una carta sobre el tablero), que se envían al modelo en el servidor a través del controlador. El servidor contiene referencias a los otros clientes desde los que están conectados los otros jugadores. El modelo se encarga de modificar su estado dependiendo de los eventos y luego avisa a todos los clientes que ocurrió un evento, indicándoles el tipo. De esta forma, los clientes saben qué parte de la interfaz deben actualizar. Para ello, realizan llamadas al servidor, quien los provee los detalles que piden, como la lista de jugadores, las cartas presentes en el tablero, etc..
 
 A continuación está el UML de la aplicación entera:
 
 ![Saboteur-UML](https://user-images.githubusercontent.com/20674466/203631311-e251d609-f829-40ba-baa1-a9ef44d33d88.png) 

La interfaz es la siguiente:

![image](https://user-images.githubusercontent.com/20674466/203631848-cba2ed12-1a36-441d-9b70-a371baa0964f.png)
