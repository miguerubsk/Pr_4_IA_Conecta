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
public class IAPlayer extends Player {

    //Profundidad hasta la que vamos a explorar el árbol de juego
    public final static int NIVEL_DEFECTO = 7;
    private static final int COLUMNAS = 7;
    private static final int FILAS = 6;
    private int m_columna = -1;
    private int alpha;
    private int beta;

    /* Nos permite cambiar entre el jugador y la maquina */
    public static int alternarJugador(int jugador) {
        if (jugador == Conecta.JUGADOR1) {
            return Conecta.JUGADOR2;
        } else {
            return Conecta.JUGADOR1;
        }
    }
    /* Parametros iniciales de la poda, corresponde a -infinito y +infinito */
    private static final int MAXIMO = Integer.MAX_VALUE;
    private static final int MINIMO = Integer.MIN_VALUE;
    private static double[] _pesos = {1, 2, 3};//Vector de pesos  

    public int buscarMovimiento(Tablero tablero, int jugador) {
        int valorSucesor, fila;
        int mejorValor = Integer.MIN_VALUE;
        int _jugadorMax = jugador;

        /* Habilitar para la poda alfa-beta */
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;

        for (int columna = 0; columna <= COLUMNAS - 1; columna++) {
            fila = FILAS - 1;
            while (fila >= 0 && tablero.obtenerCasilla(fila, columna) != 0) {
                fila--;
            }
            if (fila != -1) {//Si encontramos casilla vacia
                Tablero resultado = new Tablero(tablero);//Generamos tablero
                resultado.setButton(columna, _jugadorMax);//y colocamos ficha

                valorSucesor = AlfaBeta(resultado, alternarJugador(_jugadorMax), 1);//Valor del tablero sucesor
                //valorSucesor = MINIMAX(resultado, alternarJugador(jugador), 1);

                resultado = null;//Eliminar el tablero y nos quedamos con su valor

                if (valorSucesor > mejorValor) {//Si obtenemos un valor mayor
                    mejorValor = valorSucesor;//actualizar
                    m_columna = columna;//y nos quedamos con su columna
                    alpha = mejorValor;
                }
            }
        }
        return (m_columna);//Devolver la mejor columna

    }

    public boolean tableroLleno(Tablero tablero) {
        boolean lleno = true;
        int i = 0;
        int j;

        while (lleno && i < FILAS) {
            j = 0;
            while (lleno && j < COLUMNAS) {
                if (tablero.obtenerCasilla(i, j) == 0) {
                    lleno = false;
                } else {
                    j = j + 1;
                }
            }
            i = i + 1;
        }

        return lleno;
    }

    public int AlfaBeta(Tablero tablero, int jugador, int capa) {

        /* Casos bases */
 /* Situacion de empate */
        if (tableroLleno(tablero) && cuatroEnRaya(tablero) == 0) {
            return (0);
        }

        /* Victoria del jugador 1!! */
        if (cuatroEnRaya(tablero) == Conecta.JUGADOR1) {
            return (MAXIMO);
        }

        /* Victoria del jugador 2!! */
        if (cuatroEnRaya(tablero) == Conecta.JUGADOR2) {
            return (MINIMO);
        }

        /* Estamos en nodos terminales */
        if (capa == (NIVEL_DEFECTO)) {
            return (valoracion(tablero, jugador));//Valorar el estado terminal
        }

        int valorSucesor, fila;

        for (int columna = 0; columna <= COLUMNAS - 1; columna++) {
            fila = FILAS - 1;
            while (fila >= 0 && tablero.obtenerCasilla(fila, columna) != 0) {
                fila--;
            }
            if (fila != -1) {
                Tablero resultado = new Tablero(tablero);
                resultado.setButton(columna, jugador);

                valorSucesor = AlfaBeta(resultado, alternarJugador(jugador), (capa + 1));
                resultado = null;

                if (esCapaMAX(capa)) {//Si estamos en una capaMAX
                    alpha = maximo(alpha, valorSucesor);//Actualizamos el valor de alfa
                    if (alpha >= beta) {//o podamos
                        return alpha;
                    }
                } else {//Si es una capaMIN
                    beta = minimo(beta, valorSucesor);//Actualizar beta
                    if (beta <= alpha) {// o podamos
                        return beta;
                    }
                }
            }
        }
        if (esCapaMAX(capa)) {//Devolver a la capa superior
            return alpha;//alfa si es capaMAX
        } else {
            return beta;//beta si es capaMIN
        }
    }

    public boolean esCapaMIN(int capa) {//Si es una capa impar,es capaMIN
        return ((capa % 2) == 1);
    }

    public boolean esCapaMAX(int capa) { //Si es una capa par,es capaMAX
        return ((capa % 2) == 0);
    }

    public int maximo(int v1, int v2) {
        if (v1 > v2) {
            return (v1);
        } else {
            return (v2);
        }
    }

    public int minimo(int v1, int v2) {
        if (v1 < v2) {
            return (v1);
        } else {
            return (v2);
        }
    }

    public int valoracion(Tablero tablero, int jugador) {
        return ((int) (java.lang.Math.round(_pesos[0] * ponderacion(tablero))
                + java.lang.Math.round(_pesos[1] * parejas(tablero))
                + java.lang.Math.round(_pesos[2] * trios(tablero))));
    }

    private int ponderacion(Tablero tablero) {
        //   ponderacion del tablero: 1 2 3 4 3 2 1

        int mias = 0; // Valor de mi jugada
        int suyas = 0;//Valor de la jugada de la maquina

        int medio = (int) java.lang.Math.floor(COLUMNAS / 2);
        int peso;
        int i, j;

        // primera mitad del tablero (incrementar peso de las columnas)
        for (i = FILAS - 1; i >= 0; i--) {
            for (j = medio, peso = medio + 1; j >= 0; j--, peso--) {
                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {//Si es una ficha mia
                    mias += peso;//Incremento mi jugada con el peso de la ficha
                } else if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {//Si es ficha de la maquina
                    suyas += peso;//Incrementar su jugada con el peso de su ficha
                }
            }
        }

        // segunda mitad del tablero (decrementar peso de las columnas)
        for (i = FILAS - 1; i >= 0; i--) {
            for (j = medio + 1, peso = medio; j <= COLUMNAS - 1; j++, peso--) {
                if (tablero.obtenerCasilla(i, j) == 1) {
                    mias += peso;
                } else if (tablero.obtenerCasilla(i, j) == 2) {
                    suyas += peso;
                }
            }
        }

        return (mias - suyas);//Devolver el valor de la jugada
    }

    private int parejas(Tablero tablero) {
        // Evaluacion de los pares de fichas adyacentes

        int mias = 0; // num de pare de casillas del jugador
        int suyas = 0;

        int i, j;
        int casillaActual, casillaVecina;

        for (i = FILAS - 1; i >= 0; i--) {
            for (j = 0; j <= COLUMNAS - 1; j++) {
                if (tablero.obtenerCasilla(i, j) != 0) {
                    // Esta ocupada
                    casillaActual = tablero.obtenerCasilla(i, j);
                    if (i > 0) {
                        // Pares vertical
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
                    // Pares horizontal
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

        return (mias - suyas);
    }

    private int trios(Tablero tablero) {
        // Evaluacion de los pares de fichas adyacentes

        int mias = 0; // num de pare de casillas del jugador
        int suyas = 0;

        int i, j;
        int casillaActual, casillaVecina, casillaVecina2;

        for (i = FILAS - 1; i >= 0; i--) {
            for (j = 0; j <= COLUMNAS - 1; j++) {
                if (tablero.obtenerCasilla(i, j) != 0) {
                    // Esta ocupada
                    casillaActual = tablero.obtenerCasilla(i, j);
                    if (i > 1) {
                        // Trios en vertical
                        casillaVecina = tablero.obtenerCasilla(i - 1, j);
                        casillaVecina2 = tablero.obtenerCasilla(i - 2, j);
                        if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
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
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
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
                                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                                    suyas++;
                                }
                            }
                        }
                    }
                    // Trios en horizontal
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

        return (mias - suyas);
    }

    public int cuatroEnRaya(Tablero tablero) {
        int[][] m_tablero = tablero.boton_int;
        int i = FILAS - 1;
        int j;
        boolean encontrado = false;
        int jugador = 0;
        int casilla;
        while (!encontrado && i >= 0) {
            j = COLUMNAS - 1;
            while (!encontrado && j >= 0) {
                casilla = m_tablero[i][j];
                if (casilla != 0) {
                    // Busqueda horizontal
                    if (j - 3 >= 0) {
                        if (m_tablero[i][j - 1] == casilla
                                && m_tablero[i][j - 2] == casilla
                                && m_tablero[i][j - 3] == casilla) {
                            encontrado = true;
                            jugador = casilla;
                        }
                    }
                    // Busqueda vertical
                    if (i + 3 < FILAS) {
                        if (m_tablero[i + 1][j] == casilla
                                && m_tablero[i + 2][j] == casilla
                                && m_tablero[i + 3][j] == casilla) {
                            encontrado = true;
                            jugador = casilla;
                        } else {
                            // Busqueda diagonal 1
                            if (j - 3 >= 0) {
                                if (m_tablero[i + 1][j - 1] == casilla
                                        && m_tablero[i + 2][j - 2] == casilla
                                        && m_tablero[i + 3][j - 3] == casilla) {
                                    encontrado = true;
                                    jugador = casilla;
                                }
                            }
                            // Busqueda diagonal 2
                            if (j + 3 < COLUMNAS) {
                                if (m_tablero[i + 1][j + 1] == casilla
                                        && m_tablero[i + 2][j + 2] == casilla
                                        && m_tablero[i + 3][j + 3] == casilla) {
                                    encontrado = true;
                                    jugador = casilla;
                                }
                            }
                        }
                    }
                }
                j = j - 1;
            }
            i = i - 1;
        }
        return jugador;
    }

    /**
     * A partir del tablero fboard obtiene la mejor jugada, determinando la
     * columna donde colocar El tablero está en la variable m_tablero
     */
    @Override
    public int jugada(Grid tablero, int conecta) {

        // ...
        // Calcular la mejor columna posible donde hacer nuestra jugada
        //Pintar Ficha (sustituir 'columna' por el valor adecuado)
        //Pintar Ficha
        Tablero tableroRaiz = new Tablero(tablero.toArray());
        
        int columna = buscarMovimiento(tableroRaiz, Conecta.JUGADOR2);
        

        return tablero.checkWin(tablero.setButton(columna, Conecta.JUGADOR2), columna, conecta);

    } // jugada

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
