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
public class RandomPlayer extends Player {

    /**
     * Número de columnas del tablero
     */
    private static final int COLUMNAS = 7;
    /**
     * Número de filas del tablero
     */
    private static final int FILAS = 6;
    /**
     * Número de fichas consecutivas necesarias para ganar
     */
    private static final int CONECTA = 4;
    /**
     * Profundidad máxima a la que se descenderá en el árbol
     */
    private static final int PROFUNDIDAD_MAX = 11;

    /**
     * Contiene el tablero tal cual era antes de la jugada del enemigo
     */
    private int[][] tableroAnterior;
    /**
     * Indica si es la primera vez que se juega
     */
    private boolean primeraJugada = true;

    /**
     *
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    @Override
    public int jugada(Grid tablero, int conecta) {
        if (primeraJugada) {
            tableroAnterior = new int[FILAS][COLUMNAS];
            for (int i = 0; i < COLUMNAS; i++) {
                for (int j = 0; j < FILAS; j++) {
                    tableroAnterior[j][i] = 0;
                }
            }
            primeraJugada = false;
        }
        int[][] tableroActual = tablero.toArray();

        // ...
        // Calcular la mejor columna posible donde hacer nuestra jugada
        //Pintar Ficha (sustituir 'columna' por el valor adecuado)
        //Pintar Ficha
        int columna = 0;

        Estado estadoActual = new Estado(tableroActual, Conecta.JUGADOR1, 1);

        Pair<Integer, Integer> jugada = null;

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (tableroActual[i][j] != tableroAnterior[i][j]) {
                    jugada = new Pair<>(i, j);
                    break;
                }
            }
        }

        minimaxAlphaBeta(estadoActual, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, jugada);

        for (Estado e : estadoActual.hijos) {
            if (estadoActual.valor == e.valor) {
                columna = e.columna;
                break;
            }
        }

        int fila = tablero.setButton(columna, Conecta.JUGADOR2);
        tableroAnterior = new int[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            System.arraycopy(tablero.toArray()[i], 0, tableroAnterior[i], 0, COLUMNAS);
        }
//        print(estadoActual); //Descomentar para mostrar por consola el árbol de jugadas por niveles
        return tablero.checkWin(fila, columna, conecta);

    }

    private int minimaxAlphaBeta(Estado estadoActual, int profundidadActual, int alpha, int beta, Pair<Integer, Integer> ultimaJugada) {
        if (profundidadActual == PROFUNDIDAD_MAX || estadoActual.tablero.tableroLleno() || estadoActual.tablero.checkWin(ultimaJugada.getKey(), ultimaJugada.getValue(), CONECTA) != 0) {
            estadoActual.valor = ponderarTablero(estadoActual.tablero, ultimaJugada);
            return estadoActual.valor;
        }

        for (int i = 0; i < COLUMNAS; i++) {
            if (!estadoActual.tablero.fullColumn(i)) {
                Estado estadoSig = new Estado(estadoActual.tablero, estadoActual.alternarJugador(), profundidadActual + 1);
                Pair<Integer, Integer> jugada = new Pair<>(estadoSig.tablero.setButton(i, estadoSig.jugador), i);
                estadoActual.hijos.add(estadoSig);
                estadoSig.columna = i;
                if (estadoActual.jugador == Conecta.JUGADOR2) {
                    estadoActual.valor = Math.min(minimaxAlphaBeta(estadoSig, profundidadActual + 1, alpha, beta, jugada), estadoActual.valor);
                    beta = Math.min(beta, estadoActual.valor);
                    if (beta <= alpha) {
                        return estadoActual.valor;
                    }
                } else {
                    estadoActual.valor = Math.max(minimaxAlphaBeta(estadoSig, profundidadActual + 1, alpha, beta, jugada), estadoActual.valor);
                    alpha = Math.max(alpha, estadoActual.valor);
                    if (alpha >= beta) {
                        return estadoActual.valor;
                    }
                }
            }
        }
        return estadoActual.valor;
    }

    private int ponderarTablero(Tablero tablero, Pair<Integer, Integer> ultimaJugada) {

        switch (tablero.checkWin(ultimaJugada.getKey(), ultimaJugada.getValue(), CONECTA)) {
            case Conecta.JUGADOR2:
                return Integer.MAX_VALUE - 1;
            case Conecta.JUGADOR1:
                return Integer.MIN_VALUE + 1;
        }

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
        return new Pair<>(mias, suyas);
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

        return new Pair<>(mias, suyas);
    }

    public static void print(Estado root) {
        if (root != null) {
            Queue<Estado> cola_nivel = new LinkedList<>();
            cola_nivel.clear();
            cola_nivel.add(root);
            while (!cola_nivel.isEmpty()) {
                Estado temp = cola_nivel.remove();
                System.out.println(temp.toString());
                temp.hijos.forEach(n -> {
                    cola_nivel.add(n);
                });
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
        private int valor, nivel, columna;

        /**
         * Jugador que le toca jugar este estado.
         */
        private int jugador;

        /**
         * Constructor parametrizado.
         *
         * @param tablero contiene el tablero con la jugada hecha por el jugador
         * que lo está jugando
         * @param jugador jugador que está jugando el estado
         * @param nivel nivel en que se encuentra el estado en el árbol de
         * jugadas
         */
        public Estado(int[][] tablero, int jugador, int nivel) {
            this.hijos = new ArrayList<>();
            this.tablero = new Tablero(tablero);
            this.estadoFinal = false;
            this.jugador = jugador;
            this.nivel = nivel;
            if (jugador == Conecta.JUGADOR2) {
                this.valor = Integer.MAX_VALUE;
            } else {
                this.valor = Integer.MIN_VALUE;
            }
        }

        /**
         * Constructor parametrizado.
         *
         * @param tablero contiene el tablero con la jugada hecha por el jugador
         * que lo está jugando
         * @param jugador jugador que está jugando el estado
         * @param nivel nivel en que se encuentra el estado en el árbol de
         * jugadas
         */
        public Estado(Tablero tablero, int jugador, int nivel) {
            this.hijos = new ArrayList<>();
            this.tablero = tablero.clone();
            this.estadoFinal = false;
            this.jugador = jugador;
            this.nivel = nivel;
            if (jugador == Conecta.JUGADOR2) {
                this.valor = Integer.MAX_VALUE;
            } else {
                this.valor = Integer.MIN_VALUE;
            }
        }

        /**
         *
         * @return el jugador contrario al que juega este estado
         */
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

        private final int[][] boton_int;

        public Tablero(int[][] tablero) {
            this.boton_int = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                System.arraycopy(tablero[i], 0, this.boton_int[i], 0, COLUMNAS);
            }
        }

        public Tablero(Tablero tablero) {
            this.boton_int = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                System.arraycopy(tablero.boton_int[i], 0, this.boton_int[i], 0, COLUMNAS);
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

        public boolean tableroLleno() {

            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    if (boton_int[i][j] != 0) {
                        return false;
                    }
                }
            }

            return true;
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
                        this.boton_int[y][col] = Conecta.JUGADOR1;
                        break;
                    case Conecta.JUGADOR2:
                        this.boton_int[y][col] = Conecta.JUGADOR2;
                        break;
                    case Conecta.JUGADOR0:
                        this.boton_int[y][col] = Conecta.JUGADOR0;
                        break;
                } // switch
            } // if

            return y;

        }

    }

    public class Pair<K extends Object, V extends Object> {

        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Pair{" + "key=" + key + ", value=" + value + '}';
        }

    }

    public static int getPROFUNDIDAD_MAX() {
        return PROFUNDIDAD_MAX;
    }
    
}
