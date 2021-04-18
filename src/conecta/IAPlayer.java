/*
 * Copyright (C) 2021 Miguel González García
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package conecta;

import java.util.ArrayList;

/**
 *
 * @author José María Serrano
 * @author Cristóbal J. Carmona
 * @version 1.4 Departamento de Informática. Universidad de Jáen
 *
 * Inteligencia Artificial. 2º Curso. Grado en Ingeniería Informática
 *
 * Curso 2020-21: Se introducen obstáculos aleatorios Clase IAPlayer para
 * representar al jugador CPU que usa la poda Alfa Beta
 *
 * Esta clase es la que tenemos que implementar y completar
 *
 */
public class IAPlayer extends Player {

    private static final int COLUMNAS = 7;
    private static final int FILAS = 6;
    private static final int CONECTA = 4;
    private static final int PROFUNDIDAD = 7;

    private int alpha;
    private int beta;

    /**
     *
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    @Override
    public int jugada(Grid tablero, int conecta) {

        // Renovamos los valores cada vez que se llama al metodo
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;

        // ...
        // Calcular la mejor columna posible donde hacer nuestra jugada
        //Pintar Ficha (sustituir 'columna' por el valor adecuado)
        //Pintar Ficha
        int columna = getRandomColumn(tablero);
        
        Estado estadoActual = new Estado(tablero.toArray(), false, Conecta.JUGADOR1);
        
        construirArbolMiniMax(estadoActual, estadoActual.alternarJugador(), 0);

        return tablero.checkWin(tablero.setButton(columna, Conecta.JUGADOR2), columna, conecta);

    }

    private void construirArbolMiniMax(Estado estadoActual, int jugador, int profundidadActual) {
        if (!estadoActual.estadoFinal) {
            for (int i = 0; i < COLUMNAS; i++) {
                if (!estadoActual.tablero.fullColumn(i)) {
                    Tablero tableroAux = estadoActual.tablero.clone();
                    Estado estadoAux;
                    switch(tableroAux.checkWin(tableroAux.setButton(i, jugador), i, jugador)){
                        case 1:
                            estadoAux = new Estado(tableroAux, true, estadoActual.alternarJugador());
                            estadoAux.valor = -1;
                            estadoActual.hijos.add(estadoAux);
                            break;
                        case -1:
                            estadoAux = new Estado(tableroAux, true, estadoActual.alternarJugador());
                            estadoAux.valor = 1;
                            estadoActual.hijos.add(estadoAux);
                            break;
                        default: 
                            estadoAux = new Estado(tableroAux, false, estadoActual.alternarJugador());
                            estadoActual.hijos.add(estadoAux);
                            construirArbolMiniMax(estadoAux, jugador, profundidadActual+1);
                            break;
                    }
//                    System.err.println("Capa " + profundidadActual + " Nodo " + i + estadoActual.tablero.toString());
                }
            }
            if(estadoActual.hijos.isEmpty()){
                estadoActual.valor = 0;
                estadoActual.estadoFinal = true;
            }else{
                switch(estadoActual.jugador){
                    case -1:
                        for(Estado hijo : estadoActual.hijos){
                            if(hijo.valor > estadoActual.valor)
                                estadoActual.valor = hijo.valor;
                        }
                        break;
                    case 1:
                        for(Estado hijo : estadoActual.hijos){
                            if(hijo.valor < estadoActual.valor)
                                estadoActual.valor = hijo.valor;
                        }
                        break;
                }
            }
        }
    }

    public class Estado {

        /**
         * Vector de Estados que descienden del actual
         */
        private ArrayList<Estado> hijos;

        /**
         * Estado actual del tablero
         */
        private Tablero tablero;

        /**
         * Indica si el estado actual es estado final. Es estado final si es
         * solución o si es nodo hoja.
         */
        private boolean estadoFinal;

        /**
         * Indica el valor heurístico del estado
         */
        private int valor;

        /**
         * Jugador que le toca jugar este estado.
         */
        private int jugador;

        /**
         * Constructor parametrizado.
         *
         * @param tablero Dato que alberga el Estado
         */
        public Estado(int[][] tablero, boolean estadoFinal, int jugador) {
            this.hijos = new ArrayList<>();
            this.tablero = new Tablero(tablero);
            this.estadoFinal = estadoFinal;
            this.jugador = jugador;
            this.valor = 0;
        }

        public Estado(Tablero tablero, boolean estadoFinal, int jugador) {
            this.hijos = new ArrayList<>();
            this.tablero = tablero.clone();
            this.estadoFinal = estadoFinal;
            this.jugador = jugador;
            this.valor = 0;
        }

        private int alternarJugador() {
            if (this.jugador == 1) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public class Tablero {

        private int[][] tablero;

        public Tablero(int[][] tablero) {
            this.tablero = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    this.tablero[i][j] = tablero[i][j];
                }
            }
        }

        public Tablero(Tablero tablero) {
            this.tablero = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    this.tablero[i][j] = tablero.tablero[i][j];
                }
            }
        }

        /**
         * Compruba si la columna que se le pasa como parámetro está llena
         * @param col columna que se quiere comprobar
         * @return true si está llena
         * @return false si queda algún hueco
         */
        private boolean fullColumn(int col) {
            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (tablero[y][col] != 0)) {
                y--;
            }

            // Si y < 0, columna completa
            return (y < 0);

        }

        /**
         * // Devuelve la posición del primer hueco libre de la columna
         * @param col columna que se quiere comprobar
         * @return la posición del hueco
         * @return -2 si la columna está llena
         */
        private int topColumn(int col) {
            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (tablero[y][col] != 0)) {
                y--;
            }
            if (y < 0) {
                return -2; // Error: La columna está completa
            } else {
                return (y + 1);
            }
        }

        @Override
        protected Tablero clone() {
            return new Tablero(this);
        }

        /**
         * Escribe por pantalla el tablero
         */
        public void print() {
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    System.out.print(this.tablero[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }

        @Override
        public String toString() {
            String aux = "\n";
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    aux += this.tablero[i][j] + " ";
                }
                aux += "\n";
            }
            aux += "\n";
            return aux;
        }

        /**
         * Comprueba si el tablero se halla en un estado de fin de partida, a partir de la última jugada realizada
         * @param x fila de la última ficha
         * @param y columna de la última ficha
         * @param conecta número de fichas consecutivas necesarias para ganar
         * @return 1 si gana el jugador 1. -1 si gana el jugador 2. 0 si aún no ha ganado nadie
         */
        public int checkWin(int x, int y, int conecta) {
            /*
		 *	x fila
		 *	y columna
             */

            //Comprobar vertical
            int ganar1 = 0;
            int ganar2 = 0;
            int ganador = 0;
            boolean salir = false;
            for (int i = 0; (i < FILAS) && !salir; i++) {
                if (tablero[i][y] != Conecta.VACIO) {
                    if (tablero[i][y] == Conecta.JUGADOR1) {
                        ganar1++;
                    } else {
                        ganar1 = 0;
                    }
                    // Gana el jugador 1
                    if (ganar1 == conecta) {
                        ganador = Conecta.JUGADOR1;
                        salir = true;
                    }
                    if (!salir) {
                        if (tablero[i][y] == Conecta.JUGADOR2) {
                            ganar2++;
                        } else {
                            ganar2 = 0;
                        }
                        // Gana el jugador 2
                        if (ganar2 == conecta) {
                            ganador = Conecta.JUGADOR2;
                            salir = true;
                        }
                    }
                } else {
                    ganar1 = 0;
                    ganar2 = 0;
                }
            }
            // Comprobar horizontal
            ganar1 = 0;
            ganar2 = 0;
            for (int j = 0; (j < COLUMNAS) && !salir; j++) {
                if (tablero[x][j] != Conecta.VACIO) {
                    if (tablero[x][j] == Conecta.JUGADOR1) {
                        ganar1++;
                    } else {
                        ganar1 = 0;
                    }
                    // Gana el jugador 1
                    if (ganar1 == conecta) {
                        ganador = Conecta.JUGADOR1;
                        salir = true;
                    }
                    if (ganador != Conecta.JUGADOR1) {
                        if (tablero[x][j] == Conecta.JUGADOR2) {
                            ganar2++;
                        } else {
                            ganar2 = 0;
                        }
                        // Gana el jugador 2
                        if (ganar2 == conecta) {
                            ganador = Conecta.JUGADOR2;
                            salir = true;
                        }
                    }
                } else {
                    ganar1 = 0;
                    ganar2 = 0;
                }
            }
            // Comprobar oblicuo. De izquierda a derecha
            ganar1 = 0;
            ganar2 = 0;
            int a = x;
            int b = y;
            while (b > 0 && a > 0) {
                a--;
                b--;
            }
            while (b < COLUMNAS && a < FILAS && !salir) {
                if (tablero[a][b] != Conecta.VACIO) {
                    if (tablero[a][b] == Conecta.JUGADOR1) {
                        ganar1++;
                    } else {
                        ganar1 = 0;
                    }
                    // Gana el jugador 1
                    if (ganar1 == conecta) {
                        ganador = Conecta.JUGADOR1;
                        salir = true;
                    }
                    if (ganador != Conecta.JUGADOR1) {
                        if (tablero[a][b] == Conecta.JUGADOR2) {
                            ganar2++;
                        } else {
                            ganar2 = 0;
                        }
                        // Gana el jugador 2
                        if (ganar2 == conecta) {
                            ganador = Conecta.JUGADOR2;
                            salir = true;
                        }
                    }
                } else {
                    ganar1 = 0;
                    ganar2 = 0;
                }
                a++;
                b++;
            }
            // Comprobar oblicuo de derecha a izquierda 
            ganar1 = 0;
            ganar2 = 0;
            a = x;
            b = y;
            //buscar posición de la esquina
            while (b < COLUMNAS - 1 && a > 0) {
                a--;
                b++;
            }
            while (b > -1 && a < FILAS && !salir) {
                if (tablero[a][b] != Conecta.VACIO) {
                    if (tablero[a][b] == Conecta.JUGADOR1) {
                        ganar1++;
                    } else {
                        ganar1 = 0;
                    }
                    // Gana el jugador 1
                    if (ganar1 == conecta) {
                        ganador = Conecta.JUGADOR1;
                        salir = true;
                    }
                    if (ganador != Conecta.JUGADOR1) {
                        if (tablero[a][b] == Conecta.JUGADOR2) {
                            ganar2++;
                        } else {
                            ganar2 = 0;
                        }
                        // Gana el jugador 2
                        if (ganar2 == conecta) {
                            ganador = Conecta.JUGADOR2;
                            salir = true;
                        }
                    }
                } else {
                    ganar1 = 0;
                    ganar2 = 0;
                }
                a++;
                b--;
            }

            return ganador;
        }

        // Coloca una ficha en la columna col
        public int setButton(int col, int jugador) {

            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (this.tablero[y][col] != 0)) {
                y--;
            }

            // Si la columna no está llena, colocar la ficha
            if (y >= 0) {
                switch (jugador) {
                    case Conecta.JUGADOR1:
                        this.tablero[y][col] = 1;
                        break;
                    case Conecta.JUGADOR2:
                        this.tablero[y][col] = -1;
                        break;
                    case Conecta.JUGADOR0:
                        this.tablero[y][col] = 2;
                        break;
                } // switch
            } // if

            return y;

        }

    }

}
