/*
 * Created on 2-4-2013
 */
package conecta;

import conecta.Grid;

/**
 * @author Salvador
 *
 */
public class JugadorMaquina extends Player {

    //Profundidad hasta la que vamos a explorar el árbol de juego
    private static final int COLUMNAS = 4;
    private static final int FILAS = 4;
    private static final int CONECTA = 4;
    public final static int PROFUNDIDAD = 7;


    /* Nos permite cambiar entre el jugador y la maquina */
    public static int alternarJugador(int jugador) {
        if (jugador == Conecta.JUGADOR1) {
            return Conecta.JUGADOR2;
        }
        return Conecta.JUGADOR1;
    }
    /* Parametros iniciales de la poda, corresponde a -infinito y +infinito */
    public static int MAXIMO = Integer.MAX_VALUE;
    public static int MINIMO = Integer.MIN_VALUE;
    private static double[] _pesos = {1, 2, 3};//Vector de pesos  

    public int buscarMovimiento(Tablero tablero, int jugador) {
        int valorSucesor, fila;
        int mejorValor = MINIMO;
        int _jugadorMax = jugador;
        int m_columna = -1;

        /* Habilitar para la poda alfa-beta */
        int alfa = MINIMO;
        int beta = MAXIMO;

        for (int columna = 0; columna < COLUMNAS; columna++) {
            fila = FILAS - 1;
            while (fila >= 0 && tablero.existeFicha(fila, columna)) {
                fila--;
            }
            if (fila != -1) {//Si encontramos casilla vacia
                Tablero resultado = new Tablero(tablero);//Generamos tablero
                resultado.ponerFicha(columna, _jugadorMax);//y colocamos ficha

                valorSucesor = AlfaBeta(resultado, alternarJugador(_jugadorMax), 1, alfa, beta);//Valor del tablero sucesor

                resultado = null;//Eliminar el tablero y nos quedamos con su valor

                if (valorSucesor > mejorValor) {//Si obtenemos un valor mayor
                    mejorValor = valorSucesor;//actualizar
                    m_columna = columna;//y nos quedamos con su columna
                    alfa = mejorValor;
                }
            }
        }
        return (m_columna);//Devolver la mejor columna

    }

    public int AlfaBeta(Tablero tablero, int jugador, int nivel, int alfa, int beta) {

        /* Casos bases */
 /* Situacion de empate */
        if (tablero.tableroLleno() && tablero.cuatroEnRaya() == 0) {
            return (0);
        }

        /* Victoria del jugador 1!! */
        if (tablero.cuatroEnRaya() == Conecta.JUGADOR2) {
            return (MAXIMO);
        }

        /* Victoria del jugador 2!! */
        if (tablero.cuatroEnRaya() == Conecta.JUGADOR1) {
            return (MINIMO);
        }

        /* Estamos en nodos terminales */
        if (nivel == (PROFUNDIDAD)) {
            return (valoracion(tablero, jugador));//Valorar el estado terminal
        }

        int valorSucesor, fila;

        for (int columna = 0; columna <= COLUMNAS - 1; columna++) {
            fila = FILAS - 1;
            while (fila >= 0 && tablero.existeFicha(fila, columna)) {
                fila--;
            }
            if (fila != -1) {
                Tablero resultado = new Tablero(tablero);
                resultado.ponerFicha(columna, jugador);

                valorSucesor = AlfaBeta(resultado, alternarJugador(jugador), (nivel + 1), alfa, beta);
                resultado = null;

                if (esCapaMAX(nivel)) {//Si estamos en una capaMAX
                    alfa = maximo(alfa, valorSucesor);//Actualizamos el valor de alfa
                    if (alfa >= beta) {//o podamos
                        return alfa;
                    }
                } else {//Si es una capaMIN
                    beta = minimo(beta, valorSucesor);//Actualizar beta
                    if (beta <= alfa) {// o podamos
                        return beta;
                    }
                }
            }
        }
        if (esCapaMAX(nivel)) {//Devolver a la capa superior
            return alfa;//alfa si es capaMAX
        } else {
            return beta;//beta si es capaMIN
        }
    }

    public boolean esCapaMIN(int capa) {//Si es una capa impar,es capaMIN
        return ((capa % 2) != 0);
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
                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {//Si es una ficha mia
                    mias += peso;//Incremento mi jugada con el peso de la ficha
                } else if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {//Si es ficha de la maquina
                    suyas += peso;//Incrementar su jugada con el peso de su ficha
                }
            }
        }

        // segunda mitad del tablero (decrementar peso de las columnas)
        for (i = FILAS - 1; i >= 0; i--) {
            for (j = medio + 1, peso = medio; j <= COLUMNAS - 1; j++, peso--) {
                if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR2) {
                    mias += peso;
                } else if (tablero.obtenerCasilla(i, j) == Conecta.JUGADOR1) {
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
                if (tablero.existeFicha(i, j)) {
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
                if (tablero.existeFicha(i, j)) {
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

    /**
     * A partir del tablero fboard obtiene la mejor jugada, determinando la
     * columna donde colocar El tablero está en la variable m_tablero
     */
    @Override
    public int jugada(Grid tablero, int conecta) {
        boolean buenaTirada = false;
        int i;
        int columnaCopia = -1;
        Tablero m_tablero = new Tablero(tablero.toArray());
        while (!buenaTirada) {
            columnaCopia = buscarMovimiento(m_tablero, Conecta.JUGADOR2);

            i = FILAS - 1;
            while (!buenaTirada && i >= 0) {
                if (m_tablero.obtenerCasilla(i, columnaCopia) == 0) {
                    buenaTirada = true;
                } else {
                    i--;
                }
            }
        }

        return tablero.checkWin(tablero.setButton(columnaCopia, Conecta.JUGADOR2), columnaCopia, conecta);
        // La siguiente linea NO DEBE ser borrada
    }

    public class Tablero {

        private int[][] m_tablero;

        public Tablero(int[][] tablero) {
            this.m_tablero = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    this.m_tablero[i][j] = tablero[i][j];
                }
            }
        }

        public Tablero(Tablero tablero) {
            this.m_tablero = new int[FILAS][COLUMNAS];
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    this.m_tablero[i][j] = tablero.m_tablero[i][j];
                }
            }
        }

        public boolean existeFicha(int i, int j) {
            return m_tablero[i][j] != 0;
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
            while ((y >= 0) && (m_tablero[y][col] != 0)) {
                y--;
            }

            // Si y < 0, columna completa
            return (y < 0);

        }

        /**
         * Devulve lo que se encuentra en la posición especificada
         *
         * @param x Columna
         * @param y Fila
         * @return La ficha que esté en (x, y)
         */
        public int obtenerCasilla(int x, int y) {
            return m_tablero[x][y];
        }

        /**
         * Devuelve la posición del primer hueco libre de la columna
         *
         * @param col columna que se quiere comprobar
         * @return la posición del hueco
         * @return -2 si la columna está llena
         */
        private int topColumn(int col) {
            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (m_tablero[y][col] != 0)) {
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
                    System.out.print(this.m_tablero[i][j] + " ");
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
                    aux += this.m_tablero[i][j] + " ";
                }
                aux += "\n";
            }
            aux += "\n";
            return aux;
        }

        /**
         * Comprueba si el tablero se halla en un estado de fin de partida, a
         * partir de la última jugada realizada
         *
         * @param x fila de la última ficha
         * @param y columna de la última ficha
         * @param conecta número de fichas consecutivas necesarias para ganar
         * @return 1 si gana el jugador 1. -1 si gana el jugador 2. 0 si aún no
         * ha ganado nadie
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
                if (m_tablero[i][y] != Conecta.VACIO) {
                    if (m_tablero[i][y] == Conecta.JUGADOR1) {
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
                        if (m_tablero[i][y] == Conecta.JUGADOR2) {
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
                if (m_tablero[x][j] != Conecta.VACIO) {
                    if (m_tablero[x][j] == Conecta.JUGADOR1) {
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
                        if (m_tablero[x][j] == Conecta.JUGADOR2) {
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
                if (m_tablero[a][b] != Conecta.VACIO) {
                    if (m_tablero[a][b] == Conecta.JUGADOR1) {
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
                        if (m_tablero[a][b] == Conecta.JUGADOR2) {
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
                if (m_tablero[a][b] != Conecta.VACIO) {
                    if (m_tablero[a][b] == Conecta.JUGADOR1) {
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
                        if (m_tablero[a][b] == Conecta.JUGADOR2) {
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
         * Indica si el tablero está lleno
         */
        public boolean tableroLleno() {
            boolean lleno = true;
            int i = 0;
            int j;

            while (lleno && i < FILAS) {
                j = 0;
                while (lleno && j < COLUMNAS) {
                    if (m_tablero[i][j] == 0) {
                        lleno = false;
                    } else {
                        j = j + 1;
                    }
                }
                i = i + 1;
            }

            return lleno;
        }

        /**
         * Indica si alguno de los jugadores ha hecho cuatro en raya.
         *
         * @return 0 en el caso de que no lo haya, 1 en el caso de que lo haya
         * hecho el jugador 1, y 2 en el caso de que lo haya hecho el jugador 2.
         */
        public int cuatroEnRaya() {
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
         * Coloca una ficha en la columna col
         *
         * @param col columna en la que se quiere colocar la ficha
         * @param jugador jugador que coloca la ficha
         * @return fila en la que cae la ficha
         */
        public int ponerFicha(int col, int jugador) {

            int y = FILAS - 1;
            //Ir a la última posición de la columna	
            while ((y >= 0) && (this.m_tablero[y][col] != 0)) {
                y--;
            }

            // Si la columna no está llena, colocar la ficha
            if (y >= 0) {
                switch (jugador) {
                    case Conecta.JUGADOR1:
                        this.m_tablero[y][col] = Conecta.JUGADOR1;
                        break;
                    case Conecta.JUGADOR2:
                        this.m_tablero[y][col] = Conecta.JUGADOR2;
                        break;
                    case Conecta.JUGADOR0:
                        this.m_tablero[y][col] = Conecta.JUGADOR0;
                        break;
                } // switch
            } // if

            return y;

        }

    }
}
