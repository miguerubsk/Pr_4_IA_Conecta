/*
 * Created on 2-4-2013
 */
package Juego;

/**
 * @author Salvador
 *
 */
public class JugadorMaquina extends Jugador {

    //Profundidad hasta la que vamos a explorar el árbol de juego
    public final static int NIVEL_DEFECTO = 7;

    public JugadorMaquina(boolean esComoHumano) {
        super(esComoHumano);
    }

    /* Nos permite cambiar entre el jugador y la maquina */
    public static boolean alternarJugador(boolean jugador) {
        if (jugador) {
            return false;
        } else {
            return true;
        }
    }
    /* Parametros iniciales de la poda, corresponde a -infinito y +infinito */
    public static int MAXIMO = 20000;
    public static int MINIMO = -20000;
    private static double[] _pesos = {1,2,3};//Vector de pesos  

    public int buscarMovimiento(Tablero tablero, boolean jugador) {
        int valorSucesor, fila;
        int mejorValor = MINIMO;
        boolean _jugadorMax = jugador;

        /* Habilitar para la poda alfa-beta */
        int alfa = MINIMO;
        int beta = MAXIMO;

        for (int columna = 0; columna <= tablero.obtenerColumnas() - 1; columna++) {
            fila=tablero.obtenerFilas()-1;
            while(fila>=0 && tablero.existeFicha(fila, columna)){
                fila--;
            }
            if (fila!=-1) {//Si encontramos casilla vacia
                Tablero resultado = new Tablero(tablero);//Generamos tablero
                resultado.ponerFicha(columna, _jugadorMax);//y colocamos ficha

                valorSucesor = AlfaBeta(resultado, alternarJugador(_jugadorMax), 1, alfa, beta);//Valor del tablero sucesor
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

    public int AlfaBeta(Tablero tablero, boolean jugador, int capa, int alfa, int beta) {

        /* Casos bases */
        /* Situacion de empate */
        if (tablero.tableroLleno() && tablero.cuatroEnRaya() == 0) {
            return (0);
        }

        /* Victoria del jugador 1!! */
        if (tablero.cuatroEnRaya() == 1) {
            return (MAXIMO);
        }

        /* Victoria del jugador 2!! */
        if (tablero.cuatroEnRaya() == 2) {
            return (MINIMO);
        }

        /* Estamos en nodos terminales */
        if (capa == (NIVEL_DEFECTO)) {
            return (valoracion(tablero, jugador));//Valorar el estado terminal
        }

        int valorSucesor, fila;

        for (int columna = 0; columna <= tablero.obtenerColumnas() - 1; columna++) {
            fila=tablero.obtenerFilas()-1;
            while(fila>=0 && tablero.existeFicha(fila, columna)){
                fila--;
            }
            if (fila!=-1) {
                Tablero resultado = new Tablero(tablero);
                resultado.ponerFicha(columna, jugador);

                valorSucesor = AlfaBeta(resultado, alternarJugador(jugador), (capa + 1), alfa, beta);
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

    /* public int MINIMAX(Tablero tablero, boolean jugador, int capa) {

     if (tablero.tableroLleno() && tablero.cuatroEnRaya()==0 ) {
     return (0);
     }

     if (tablero.cuatroEnRaya() == 1) {
     return (MAXIMO);
     }

     if (tablero.cuatroEnRaya() == 2) {
     return (MINIMO);
     }

       
     if (capa == (NIVEL_DEFECTO)) {
     return (valoracion(tablero, jugador));
     }

     int valor, valorSucesor;
     if (esCapaMIN(capa)) {
     valor = MAXIMO;
     } else {
     valor = MINIMO;
     }

     for (int fila = 0; fila <= tablero.obtenerFilas()-1; fila++) {
     for (int columna = 0; columna <= tablero.obtenerColumnas()-1; columna++) {
     if (tablero.existeFicha(fila, columna) == false) {
     Tablero resultado = new Tablero(tablero);
     resultado.ponerFicha(columna, jugador);

     valorSucesor = MINIMAX(resultado, alternarJugador(jugador), (capa + 1));
     resultado = null;

     if (esCapaMAX(capa)) {
     valor = maximo(valor, valorSucesor);
     } else {
     valor = minimo(valor, valorSucesor);
     }
     }
     }
     }
     return (valor);
     }*/
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

    public int valoracion(Tablero tablero, boolean jugador) {
        return ((int) (java.lang.Math.round(_pesos[0] * ponderacion(tablero))
                + java.lang.Math.round(_pesos[1] * parejas(tablero))
                + java.lang.Math.round(_pesos[2] * trios(tablero))));
    }

    private int ponderacion(Tablero tablero) {
        //   ponderacion del tablero: 1 2 3 4 3 2 1

        int mias = 0; // Valor de mi jugada
        int suyas = 0;//Valor de la jugada de la maquina

        int medio = (int) java.lang.Math.floor(tablero.obtenerColumnas() / 2);
        int peso;
        int i, j;


        // primera mitad del tablero (incrementar peso de las columnas)
        for (i = tablero.obtenerFilas() - 1; i >= 0; i--) {
            for (j = medio, peso = medio + 1; j >= 0; j--, peso--) {
                if (tablero.obtenerCasilla(i, j) == 1) {//Si es una ficha mia
                    mias += peso;//Incremento mi jugada con el peso de la ficha
                } else if (tablero.obtenerCasilla(i, j) == 2) {//Si es ficha de la maquina
                    suyas += peso;//Incrementar su jugada con el peso de su ficha
                }
            }
        }

        // segunda mitad del tablero (decrementar peso de las columnas)
        for (i = tablero.obtenerFilas() - 1; i >= 0; i--) {
            for (j = medio + 1, peso = medio; j <= tablero.obtenerColumnas() - 1; j++, peso--) {
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

        for (i = tablero.obtenerFilas() - 1; i >= 0; i--) {
            for (j = 0; j <= tablero.obtenerColumnas() - 1; j++) {
                if (tablero.existeFicha(i, j)) {
                    // Esta ocupada
                    casillaActual = tablero.obtenerCasilla(i, j);
                    if (i > 0) {
                        // Pares vertical
                        casillaVecina = tablero.obtenerCasilla(i - 1, j);
                        if (casillaActual == casillaVecina) {
                            if (tablero.obtenerCasilla(i, j) == 1) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == 2) {
                                suyas++;
                            }
                        }
                        // Pares en diagonal /  por arriba derecha
                        if (j < tablero.obtenerColumnas() - 1 && i > 0) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j + 1);
                            if (casillaActual == casillaVecina) {
                                if (tablero.obtenerCasilla(i, j) == 1) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == 2) {
                                    suyas++;
                                }
                            }
                        }

                        // Pares en diagonal \ por arriba izquierda
                        if (j > 0 && i > 0) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j - 1);
                            if (casillaActual == casillaVecina) {
                                if (tablero.obtenerCasilla(i, j) == 1) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == 2) {
                                    suyas++;
                                }
                            }
                        }
                    }
                    // Pares horizontal
                    if (j > 0) {
                        casillaVecina = tablero.obtenerCasilla(i, j - 1);
                        if (casillaActual == casillaVecina) {
                            if (tablero.obtenerCasilla(i, j) == 1) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == 2) {
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

        for (i = tablero.obtenerFilas() - 1; i >= 0; i--) {
            for (j = 0; j <= tablero.obtenerColumnas() - 1; j++) {
                if (tablero.existeFicha(i, j)) {
                    // Esta ocupada
                    casillaActual = tablero.obtenerCasilla(i, j);
                    if (i > 1) {
                        // Trios en vertical
                        casillaVecina = tablero.obtenerCasilla(i - 1, j);
                        casillaVecina2 = tablero.obtenerCasilla(i - 2, j);
                        if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                            if (tablero.obtenerCasilla(i, j) == 1) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == 2) {
                                suyas++;
                            }
                        }
                        // Trios en diagonal /  por arriba derecha
                        if (j < tablero.obtenerColumnas() - 2 && i > 1) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j + 1);
                            casillaVecina2 = tablero.obtenerCasilla(i - 2, j + 2);
                            if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                                if (tablero.obtenerCasilla(i, j) == 1) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == 2) {
                                    suyas++;
                                }
                            }
                        }

                        // Trios en diagonal \ por arriba izquierda
                        if (j > 1 && i > 1) {
                            casillaVecina = tablero.obtenerCasilla(i - 1, j - 1);
                            casillaVecina2 = tablero.obtenerCasilla(i - 2, j - 2);
                            if (casillaActual == casillaVecina && casillaActual == casillaVecina2) {
                                if (tablero.obtenerCasilla(i, j) == 1) {
                                    mias++;
                                }
                                if (tablero.obtenerCasilla(i, j) == 2) {
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
                            if (tablero.obtenerCasilla(i, j) == 1) {
                                mias++;
                            }
                            if (tablero.obtenerCasilla(i, j) == 2) {
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
    public void run() {
        boolean buenaTirada = false;
        int i;
        int columnaCopia = -1;

        while (!buenaTirada) {
            columnaCopia = buscarMovimiento(m_tablero, true);

            i = m_tablero.obtenerFilas() - 1;
            while (!buenaTirada && i >= 0) {
                if (m_tablero.obtenerCasilla(i, columnaCopia) == 0) {
                    buenaTirada = true;
                } else {
                    i--;
                }
            }
        } 

        m_columna = columnaCopia;
        // La siguiente linea NO DEBE ser borrada
        isDone(true);
    }
}
