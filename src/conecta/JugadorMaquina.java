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

/**
 * @author Miguel González García
 *
 */
public class JugadorMaquina extends Player {

    //Profundidad hasta la que vamos a explorar el árbol de juego
    public final static int NIVEL_DEFECTO = 7;
    private static final int COLUMNAS = 7;
    private static final int FILAS = 6;
    private int m_columna = -1;
    private int alfa;
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

    public int buscarMovimiento(Grid tablero, int jugador) {
        int valorSucesor, fila;
        int mejorValor = Integer.MIN_VALUE;
        int _jugadorMax = jugador;

        /* Habilitar para la poda alfa-beta */
        alfa = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;

        for (int columna = 0; columna <= tablero.getColumnas() - 1; columna++) {
            fila = tablero.getFilas() - 1;
            while (fila >= 0 && tablero.getButton(fila, columna) != 0) {
                fila--;
            }
            if (fila != -1) {//Si encontramos casilla vacia
                Grid resultado = new Grid(tablero.getFilas(), tablero.getColumnas(), "", "", "");//Generamos tablero
                resultado.setButton(columna, _jugadorMax);//y colocamos ficha

                valorSucesor = AlfaBeta(resultado, alternarJugador(_jugadorMax), 1);//Valor del tablero sucesor
                //valorSucesor = MINIMAX(resultado, alternarJugador(jugador), 1);

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

    public boolean tableroLleno(Grid tablero) {
        boolean lleno = true;
        int i = 0;
        int j;

        while (lleno && i < tablero.getFilas()) {
            j = 0;
            while (lleno && j < tablero.getColumnas()) {
                if (tablero.getButton(i, j) == 0) {
                    lleno = false;
                } else {
                    j = j + 1;
                }
            }
            i = i + 1;
        }

        return lleno;
    }

    public int AlfaBeta(Grid tablero, int jugador, int capa) {

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

        for (int columna = 0; columna <= tablero.getColumnas() - 1; columna++) {
            fila = tablero.getFilas() - 1;
            while (fila >= 0 && tablero.getButton(fila, columna) != 0) {
                fila--;
            }
            if (fila != -1) {
                Grid resultado = new Grid(tablero.getFilas(), tablero.getColumnas(), "", "", "");
                resultado.setButton(columna, jugador);

                valorSucesor = AlfaBeta(resultado, alternarJugador(jugador), (capa + 1));
                resultado = null;

                if (esCapaMAX(capa)) {//Si estamos en una capaMAX
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
        if (esCapaMAX(capa)) {//Devolver a la capa superior
            return alfa;//alfa si es capaMAX
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

    public int valoracion(Grid tablero, int jugador) {
        return ((int) (java.lang.Math.round(_pesos[0] * ponderacion(tablero))
                + java.lang.Math.round(_pesos[1] * parejas(tablero))
                + java.lang.Math.round(_pesos[2] * trios(tablero))));
    }

    private int ponderacion(Grid tablero) {
        //   ponderacion del tablero: 1 2 3 4 3 2 1

        int mias = 0; // Valor de mi jugada
        int suyas = 0;//Valor de la jugada de la maquina

        int medio = (int) java.lang.Math.floor(tablero.getColumnas() / 2);
        int peso;
        int i, j;

        // primera mitad del tablero (incrementar peso de las columnas)
        for (i = tablero.getFilas() - 1; i >= 0; i--) {
            for (j = medio, peso = medio + 1; j >= 0; j--, peso--) {
                if (tablero.getButton(i, j) == Conecta.JUGADOR1) {//Si es una ficha mia
                    mias += peso;//Incremento mi jugada con el peso de la ficha
                } else if (tablero.getButton(i, j) == Conecta.JUGADOR2) {//Si es ficha de la maquina
                    suyas += peso;//Incrementar su jugada con el peso de su ficha
                }
            }
        }

        // segunda mitad del tablero (decrementar peso de las columnas)
        for (i = tablero.getFilas() - 1; i >= 0; i--) {
            for (j = medio + 1, peso = medio; j <= tablero.getColumnas() - 1; j++, peso--) {
                if (tablero.getButton(i, j) == 1) {
                    mias += peso;
                } else if (tablero.getButton(i, j) == 2) {
                    suyas += peso;
                }
            }
        }

        return (mias - suyas);//Devolver el valor de la jugada
    }

    private int parejas(Grid tablero) {
        // Evaluacion de los pares de fichas adyacentes

        int mias = 0; // num de pare de casillas del jugador
        int suyas = 0;

        int i, j;
        int casillaActual, casillaVecina;

        for (i = tablero.getFilas() - 1; i >= 0; i--) {
            for (j = 0; j <= tablero.getColumnas() - 1; j++) {
                if (tablero.getButton(i, j) != 0) {
                    // Esta ocupada
                    casillaActual = tablero.getButton(i, j);
                    if (i > 0) {
                        // Pares vertical
                        casillaVecina = tablero.getButton(i - 1, j);
                        if (casillaActual == casillaVecina) {
                            if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.getButton(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                        // Pares en diagonal /  por arriba derecha
                        if (j < tablero.getColumnas() - 1 && i > 0) {
                            casillaVecina = tablero.getButton(i - 1, j + 1);
                            if (casillaActual == casillaVecina) {
                                if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.getButton(i, j) == Conecta.JUGADOR1) {
                                    suyas++;
                                }
                            }
                        }

                        // Pares en diagonal \ por arriba izquierda
                        if (j > 0 && i > 0) {
                            casillaVecina = tablero.getButton(i - 1, j - 1);
                            if (casillaActual == casillaVecina) {
                                if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.getButton(i, j) == Conecta.JUGADOR1) {
                                    suyas++;
                                }
                            }
                        }
                    }
                    // Pares horizontal
                    if (j > 0) {
                        casillaVecina = tablero.getButton(i, j - 1);
                        if (casillaActual == casillaVecina) {
                            if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.getButton(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                    }
                }
            }
        }

        return (mias - suyas);
    }

    private int trios(Grid tablero) {
        // Evaluacion de los pares de fichas adyacentes

        int mias = 0; // num de pare de casillas del jugador
        int suyas = 0;

        int i, j;
        int casillaActual, casillaVecina, casillaVecina2;

        for (i = tablero.getFilas() - 1; i >= 0; i--) {
            for (j = 0; j <= tablero.getColumnas() - 1; j++) {
                if (tablero.getButton(i, j) != 0) {
                    // Esta ocupada
                    casillaActual = tablero.getButton(i, j);
                    if (i > 1) {
                        // Trios en vertical
                        casillaVecina = tablero.getButton(i - 1, j);
                        casillaVecina2 = tablero.getButton(i - 2, j);
                        if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                            if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                suyas++;
                            }
                        }
                        // Trios en diagonal /  por arriba derecha
                        if (j < tablero.getColumnas() - 2 && i > 1) {
                            casillaVecina = tablero.getButton(i - 1, j + 1);
                            casillaVecina2 = tablero.getButton(i - 2, j + 2);
                            if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                                if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                    suyas++;
                                }
                            }
                        }

                        // Trios en diagonal \ por arriba izquierda
                        if (j > 1 && i > 1) {
                            casillaVecina = tablero.getButton(i - 1, j - 1);
                            casillaVecina2 = tablero.getButton(i - 2, j - 2);
                            if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                                if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                    mias++;
                                }
                                if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                    suyas++;
                                }
                            }
                        }
                    }
                    // Trios en horizontal
                    if (j > 1) {
                        casillaVecina = tablero.getButton(i, j - 1);
                        casillaVecina2 = tablero.getButton(i, j - 2);
                        if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                            if (tablero.getButton(i, j) == Conecta.JUGADOR2) {
                                mias++;
                            }
                            if (tablero.getButton(i, j) == Conecta.JUGADOR1) {
                                suyas++;
                            }
                        }
                    }
                }
            }
        }

        return (mias - suyas);
    }

    public int cuatroEnRaya(Grid tablero) {
        int m_numFilas = tablero.getFilas();
        int m_numColumnas = tablero.getColumnas();
        int[][] m_tablero = tablero.toArray();
        int i = m_numFilas - 1;
        int j;
        boolean encontrado = false;
        int jugador = 0;
        int casilla;
        while (!encontrado && i >= 0) {
            j = m_numColumnas - 1;
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
                    if (i + 3 < m_numFilas) {
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
                            if (j + 3 < m_numColumnas) {
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
        boolean buenaTirada = false;
        int i;
        int columnaCopia = -1;

        while (!buenaTirada) {
            columnaCopia = buscarMovimiento(tablero, Conecta.JUGADOR2);

            i = tablero.getFilas() - 1;
            while (!buenaTirada && i >= 0) {
                if (tablero.getButton(i, columnaCopia) == 0) {
                    buenaTirada = true;
                } else {
                    i--;
                }
            }
        }

        m_columna = columnaCopia;
        return tablero.checkWin(tablero.setButton(m_columna, Conecta.JUGADOR2), m_columna, conecta);
    }

}
