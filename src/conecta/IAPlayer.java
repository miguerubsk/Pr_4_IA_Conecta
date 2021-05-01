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
import java.util.LinkedList;
import java.util.Queue;
import javafx.util.Pair;

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
    private static final int PROFUNDIDAD_MAX = 8;

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

        Estado estadoActual = new Estado(tablero.toArray(), Conecta.JUGADOR1, 1);

        construirArbolMiniMax(estadoActual, 1);

        for (Estado e : estadoActual.hijos) {
            if (estadoActual.valor == e.valor) {
                columna = e.columna;
            }
        }

//        print(estadoActual);
        return tablero.checkWin(tablero.setButton(columna, Conecta.JUGADOR2), columna, conecta);

    }

    private void construirArbolMiniMax(Estado estadoActual, int profundidadActual) {
        if (!estadoActual.estadoFinal && profundidadActual <= PROFUNDIDAD_MAX) {
            for (int i = 0; i < COLUMNAS; i++) {
                if (!estadoActual.tablero.fullColumn(i)) {
                    Estado estadoSig = new Estado(estadoActual.tablero, estadoActual.alternarJugador(), profundidadActual + 1);
                    estadoSig.columna = i;
                    int ganador = estadoSig.tablero.checkWin(estadoSig.tablero.setButton(i, estadoSig.jugador), i, CONECTA);
                    switch (ganador) {
                        case Conecta.JUGADOR1:
                            estadoSig.setEstadoFinal();
                            estadoSig.setHayGanador();
                            estadoSig.valor = Integer.MIN_VALUE;
                            estadoActual.hijos.add(estadoSig);
                            break;
                        case Conecta.JUGADOR2:
                            estadoSig.setEstadoFinal();
                            estadoSig.setHayGanador();
                            estadoSig.valor = Integer.MAX_VALUE;
                            estadoActual.hijos.add(estadoSig);
                            break;
                        default:
                            estadoActual.hijos.add(estadoSig);
                            construirArbolMiniMax(estadoSig, profundidadActual + 1);
                            break;
                    }
                }
            }
            if (estadoActual.hijos.isEmpty() && !estadoActual.estadoFinal) {
                estadoActual.valor = 0;
                estadoActual.setEstadoFinal();
            }
        }
        if (profundidadActual == PROFUNDIDAD_MAX) {
            estadoActual.setEstadoFinal();
        }

        if (estadoActual.estadoFinal && !estadoActual.hayGanador) {
            estadoActual.valor = ponderarTablero(estadoActual.tablero);
        } else {
            if (!estadoActual.hayGanador) {
                switch (estadoActual.jugador) {
                    case Conecta.JUGADOR2:
                        for (Estado hijo : estadoActual.hijos) {
                            if (hijo.valor > estadoActual.valor) {
                                estadoActual.valor = hijo.valor;
                            }
                        }
                        break;
                    case Conecta.JUGADOR1:
                        for (Estado hijo : estadoActual.hijos) {
                            if (hijo.valor < estadoActual.valor) {
                                estadoActual.valor = hijo.valor;
                            }
                        }
                        break;
                }
            }
        }

        estadoActual.tablero = null; //eliminamos el tablero para ahorrar memoria
    }

    private int ponderarTablero(Tablero tablero) {
        Pair<Integer, Integer> trios = trios(tablero);
        Pair<Integer, Integer> pares = pares(tablero);
        int yo = (trios.getKey() * 1000 + pares.getKey() * 100);
        int enemigo = (trios.getValue() * 1000 + pares.getValue() * 100);

        return (yo - enemigo);
    }

    /**
     * Comprueba la cantidad de trios propios que existen en el tablero
     *
     * @param tablero tablero del que se quieren comprobar los trios
     * @return Pair que contine los trios del jugador como clave y los del
     * enemigo como valor
     */
    private Pair<Integer, Integer> trios(Tablero tablero) {

        int mias = 0; // num de trios de fichas del jugador
        int suyas = 0;
        int i, j;
        int casillaActual, casillaVecina, casillaVecina2;

        for (i = FILAS - 1; i >= 0; i--) {
            for (j = 0; j <= COLUMNAS - 1; j++) {
                if (tablero.existeFicha(i, j)) {
                    // Esta ocupada
                    casillaActual = tablero.obtenerCasilla(i, j);
                    if (i > 1) {
                        // Trios en vertical |
                        casillaVecina = tablero.obtenerCasilla(i - 1, j);
                        casillaVecina2 = tablero.obtenerCasilla(i - 2, j);
                        if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                        // Trios en diagonal /  por arriba derecha
                        if (j < COLUMNAS - 2 && i > 1) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j + 1);
                            casillaVecina2 = tablero.obtenerCasilla(i - 2, j + 2);
                            if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                    suyas++;
                                }
                            }
                        }

                        // Trios en diagonal \ por arriba izquierda
                        if (j > 1 && i > 1) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j - 1);
                            casillaVecina2 = tablero.obtenerCasilla(i - 2, j - 2);
                            if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                    suyas++;
                                }
                            }
                        }
                    }
                    // Trios en horizontal -
                    if (j > 1) {
                        casillaVecina = tablero.obtenerCasilla(i, j - 1);
                        casillaVecina2 = tablero.obtenerCasilla(i, j - 2);
                        if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                    }
                }
            }
        }

        Pair<Integer, Integer> par = new Pair<>(mias, suyas);
        return par;
    }

    /**
     * Comprueba el número de pares propios en el tablero
     *
     * @param tablero tablero que se quiere comprobar
     * @return Pair que contine los pares del jugador como clave y los del
     * enemigo como valor
     */
    private Pair<Integer, Integer> pares(Tablero tablero) {

        int mias = 0; // num de pares de casillas del jugador
        int suyas = 0;

        int i, j;
        int casillaActual, casillaVecina;

        for (i = FILAS - 1; i >= 0; i--) {
            for (j = 0; j <= COLUMNAS - 1; j++) {
                if (tablero.existeFicha(i, j)) {
                    // Esta ocupada
                    casillaActual = tablero.obtenerCasilla(i, j);
                    if (i > 0) {
                        // Pares vertical |
                        casillaVecina = tablero.obtenerCasilla(i - 1, j);
                        if (casillaActual == casillaVecina) {
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                        // Pares en diagonal /  por arriba derecha
                        if (j < COLUMNAS - 1 && i > 0) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j + 1);
                            if (casillaActual == casillaVecina) {
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                    suyas++;
                                }
                            }
                        }

                        // Pares en diagonal \ por arriba izquierda
                        if (j > 0 && i > 0) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j - 1);
                            if (casillaActual == casillaVecina) {
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                    suyas++;
                                }
                            }
                        }
                    }
                    // Pares horizontal -
                    if (j > 0) {
                        casillaVecina = tablero.obtenerCasilla(i, j - 1);
                        if (casillaActual == casillaVecina) {
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                    }
                }
            }
        }

        Pair<Integer, Integer> par = new Pair<>(mias, suyas);
        return par;
    }

    public static void print(Estado root) {
        if (root != null) {
            Queue<Estado> cola_nivel = new LinkedList<>();
            cola_nivel.clear();
            cola_nivel.add(root);
            while (!cola_nivel.isEmpty()) {
                Estado temp = cola_nivel.remove();
                System.out.println(temp.toString());
                for (Estado n : temp.hijos) {
                    cola_nivel.add(n);
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
        private boolean estadoFinal, hayGanador;

        /**
         * Indica el valor heurístico del estado
         */
        private int valor, nivel, columna;

        /**
         * Jugador que le toca jugar este estado.
         */
        private int jugador;

        /**
         * Constructor parametrizado.
         *
         * @param tablero Dato que alberga el Estado
         */
        public Estado(int[][] tablero, int jugador, int nivel) {
            this.hijos = new ArrayList<>();
            this.tablero = new Tablero(tablero);
            this.estadoFinal = false;
            this.jugador = jugador;
            this.nivel = nivel;
            this.hayGanador = false;
            if (jugador == Conecta.JUGADOR1) {
                this.valor = Integer.MAX_VALUE;
            } else {
                this.valor = Integer.MIN_VALUE;
            }
        }

        public Estado(Tablero tablero, int jugador, int nivel) {
            this.hijos = new ArrayList<>();
            this.tablero = tablero.clone();
            this.estadoFinal = false;
            this.jugador = jugador;
            this.nivel = nivel;
            this.hayGanador = false;
            if (jugador == Conecta.JUGADOR1) {
                this.valor = Integer.MAX_VALUE;
            } else {
                this.valor = Integer.MIN_VALUE;
            }
        }

        private void setEstadoFinal() {
            this.estadoFinal = true;
        }

        private void setHayGanador() {
            this.estadoFinal = true;
        }

        private int alternarJugador() {
            if (this.jugador == Conecta.JUGADOR1) {
                return Conecta.JUGADOR2;
            } else {
                return Conecta.JUGADOR1;
            }
        }

        @Override
        public String toString() {
            String player;
            if (jugador == Conecta.JUGADOR1) {
                player = "Humano";
            } else {
                player = "Máquina";
            }
            return "Estado{" + "Nivel= " + nivel + ", estadoFinal= " + estadoFinal + ", valor= " + valor + ", jugador= " + player + "}\nTablero=" + tablero.toString();
        }

    }

    public class Tablero {

        private int[][] boton_int;

        public Tablero(int[][] tablero) {
            this.boton_int = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    this.boton_int[i][j] = tablero[i][j];
                }
            }
        }

        public Tablero(Tablero tablero) {
            this.boton_int = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    this.boton_int[i][j] = tablero.boton_int[i][j];
                }
            }
        }

        /**
         * Compruba si la columna que se le pasa como parámetro está llena
         *
         * @param col columna que se quiere comprobar
         * @return true si está llena
         * @return false si queda algún hueco
         */
        private boolean fullColumn(int col) {
            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (boton_int[y][col] != 0)) {
                y--;
            }

            // Si y < 0, columna completa
            return (y < 0);

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
                    System.out.print(this.boton_int[i][j] + " ");
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
                    aux += this.boton_int[i][j] + " ";
                }
                aux += "\n";
            }
            aux += "\n";
            return aux;
        }

        private boolean existeFicha(int fila, int columna) {
            return this.boton_int[fila][columna] != Conecta.VACIO;
        }

        private int obtenerCasilla(int fila, int columna) {
            return this.boton_int[fila][columna];
        }

        /**
         * Comprueba si el tablero se halla en un estado de fin de partida, a
         * partir de la última jugada realizada
         *
         * @param fila fila de la última ficha
         * @param columna columna de la última ficha
         * @param conecta número de fichas consecutivas necesarias para ganar
         * @return 1 si gana el jugador 1. -1 si gana el jugador 2. 0 si aún no
         * ha ganado nadie
         */
        public int checkWin(int fila, int columna, int conecta) {
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
                if (boton_int[i][columna] != Conecta.VACIO) {
                    if (boton_int[i][columna] == Conecta.JUGADOR1) {
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
                        if (boton_int[i][columna] == Conecta.JUGADOR2) {
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
                if (boton_int[fila][j] != Conecta.VACIO) {
                    if (boton_int[fila][j] == Conecta.JUGADOR1) {
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
                        if (boton_int[fila][j] == Conecta.JUGADOR2) {
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
            int a = fila;
            int b = columna;
            while (b > 0 && a > 0) {
                a--;
                b--;
            }
            while (b < COLUMNAS && a < FILAS && !salir) {
                if (boton_int[a][b] != Conecta.VACIO) {
                    if (boton_int[a][b] == Conecta.JUGADOR1) {
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
                        if (boton_int[a][b] == Conecta.JUGADOR2) {
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
            a = fila;
            b = columna;
            //buscar posición de la esquina
            while (b < COLUMNAS - 1 && a > 0) {
                a--;
                b++;
            }
            while (b > -1 && a < FILAS && !salir) {
                if (boton_int[a][b] != Conecta.VACIO) {
                    if (boton_int[a][b] == Conecta.JUGADOR1) {
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
                        if (boton_int[a][b] == Conecta.JUGADOR2) {
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

        /**
         * Coloca una ficha en la columna col para el jugador
         *
         * @param col columna en la que se coloca la ficha
         * @param jugador jugador al que pertenece la ficha
         * @return fila en la que se coloca la ficha o -1 si no se ha podido
         * colocar
         */
        public int setButton(int col, int jugador) {

            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (this.boton_int[y][col] != 0)) {
                y--;
            }

            // Si la columna no está llena, colocar la ficha
            if (y >= 0) {
                switch (jugador) {
                    case Conecta.JUGADOR1:
                        this.boton_int[y][col] = 1;
                        break;
                    case Conecta.JUGADOR2:
                        this.boton_int[y][col] = -1;
                        break;
                    case Conecta.JUGADOR0:
                        this.boton_int[y][col] = 2;
                        break;
                } // switch
            } // if

            return y;

        }

    }

}
