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
import java.util.List;

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

    private static final int COLUMNAS = 4;
    private static final int FILAS = 4;
    private static final int CONECTA = 4;
    private static final int PROFUNDIDAD_MAX = 7;

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

        Estado estadoActual = new Estado(tablero.toArray(), Conecta.JUGADOR2);

        construirArbolMiniMax(estadoActual, 0);

        print(estadoActual);

        return tablero.checkWin(tablero.setButton(columna, Conecta.JUGADOR2), columna, conecta);

    }

    private void construirArbolMiniMax(Estado estadoActual, int profundidadActual) {
        if (!estadoActual.estadoFinal && profundidadActual <= PROFUNDIDAD_MAX) {
            for (int i = 0; i < COLUMNAS; i++) {
                if (!estadoActual.tablero.fullColumn(i)) {
                    Estado estadoSig = new Estado(estadoActual.tablero, estadoActual.alternarJugador());
                    int ganador = estadoSig.tablero.checkWin(estadoSig.tablero.setButton(i, estadoSig.jugador), i, CONECTA);
                    switch (ganador) {
                        case Conecta.JUGADOR1:
                            estadoSig.setEstadoFinal();
                            estadoSig.valor = -1;
                            estadoActual.hijos.add(estadoSig);
                            break;
                        case Conecta.JUGADOR2:
                            estadoSig.setEstadoFinal();
                            estadoSig.valor = 1;
                            estadoActual.hijos.add(estadoSig);
                            break;
                        default:
                            estadoActual.hijos.add(estadoSig);
                            construirArbolMiniMax(estadoSig, profundidadActual + 1);
                            break;
                    }
                }
            }
            if (estadoActual.hijos.isEmpty()) {
                estadoActual.valor = 0;
                estadoActual.setEstadoFinal();
            }
        }
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

    public static void print(Estado root) {
        List<List<String>> lines = new ArrayList<List<String>>();

        List<Estado> level = new ArrayList<Estado>();
        List<Estado> next = new ArrayList<Estado>();

        level.add(root);
        int nn = 1;

        int widest = 0;

        while (nn != 0) {
            List<String> line = new ArrayList<String>();

            nn = 0;

            for (Estado n : level) {
                if (n == null) {
                    line.add(null);

                    next.add(null);
                    next.add(null);
                } else {
                    String aa = n.toString();
                    line.add(aa);
                    if (aa.length() > widest) {
                        widest = aa.length();
                    }

                    for (Estado aux : n.hijos) {
                        next.add(aux);
                        nn++;
                    }
                }
            }

            if (widest % 2 == 1) {
                widest++;
            }

            lines.add(line);

            List<Estado> tmp = level;
            level = next;
            next = tmp;
            next.clear();
        }

        int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
        for (int i = 0; i < lines.size(); i++) {
            List<String> line = lines.get(i);
            int hpw = (int) Math.floor(perpiece / 2f) - 1;

            if (i > 0) {
                for (int j = 0; j < line.size(); j++) {

                    // split node
                    char c = ' ';
                    if (j % 2 == 1) {
                        if (line.get(j - 1) != null) {
                            c = (line.get(j) != null) ? '┴' : '┘';
                        } else {
                            if (j < line.size() && line.get(j) != null) {
                                c = '└';
                            }
                        }
                    }
                    System.out.print(c);

                    // lines and spaces
                    if (line.get(j) == null) {
                        for (int k = 0; k < perpiece - 1; k++) {
                            System.out.print(" ");
                        }
                    } else {

                        for (int k = 0; k < hpw; k++) {
                            System.out.print(j % 2 == 0 ? " " : "─");
                        }
                        System.out.print(j % 2 == 0 ? "┌" : "┐");
                        for (int k = 0; k < hpw; k++) {
                            System.out.print(j % 2 == 0 ? "─" : " ");
                        }
                    }
                }
                System.out.println();
            }

            // print line of numbers
            for (int j = 0; j < line.size(); j++) {

                String f = line.get(j);
                if (f == null) {
                    f = "";
                }
                int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
                int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

                // a number
                for (int k = 0; k < gap1; k++) {
                    System.out.print(" ");
                }
                System.out.print(f);
                for (int k = 0; k < gap2; k++) {
                    System.out.print(" ");
                }
            }
            System.out.println();

            perpiece /= 2;
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
        public Estado(int[][] tablero, int jugador) {
            this.hijos = new ArrayList<>();
            this.tablero = new Tablero(tablero);
            this.estadoFinal = false;
            this.jugador = jugador;
            if (jugador == Conecta.JUGADOR1) {
                this.valor = Integer.MAX_VALUE;
            } else {
                this.valor = Integer.MIN_VALUE;
            }
        }

        public Estado(Tablero tablero, int jugador) {
            this.hijos = new ArrayList<>();
            this.tablero = tablero.clone();
            this.estadoFinal = false;
            this.jugador = jugador;
            if (jugador == Conecta.JUGADOR1) {
                this.valor = Integer.MAX_VALUE;
            } else {
                this.valor = Integer.MIN_VALUE;
            }
        }

        private void setEstadoFinal() {
            this.estadoFinal = false;
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
            return "Estado{" + "estadoFinal= " + estadoFinal + ", valor= " + valor + ", jugador= " + jugador + ", tablero=" + tablero.toString() + '}';
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
