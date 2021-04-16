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
 *
 * @author José María Serrano
 * @author Cristóbal J. Carmona
 * @version 1.4 Departamento de Informática. Universidad de Jáen
 *
 * Inteligencia Artificial. 2º Curso. Grado en Ingeniería Informática
 *
 * Curso 2020-21: Se introducen obstáculos aleatorios
 * Clase IAPlayer para representar al jugador CPU que usa la poda Alfa
 * Beta
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
    private Estado raiz;
    
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

        return tablero.checkWin(tablero.setButton(columna, Conecta.JUGADOR2), columna, conecta);

    }

    public class Estado {
        
        /**
         * Vector de Estados que descienden del actual
         */
        private Estado[] hijos;
        
        /**
         * Estado actual del tablero
         */
        private int[][] tablero;
        
        /**
         * Indica si el estado actual es estado final.
         * Es estado final si es solución o si es nodo hoja.
         */
        private boolean estadoFinal;
        
        /**
         * Indica si el estado actual es solución.
         */
        private boolean solucion;
        
        /**
         * Jugador que le toca jugar este estado.
         */
        private int jugador;
        
        /**
         * Constructor parametrizado.
         * @param tablero Dato que alberga el Estado
         */
        public Estado(int[][] tablero) {
            this.hijos = new Estado[COLUMNAS];
            for (int i = 0; i < this.hijos.length; i++)
                this.hijos[i] = null;
            this.tablero = tablero;
            this.estadoFinal = false;
            this.solucion = false;
            this.jugador = 0; // Jugador inválido
            
        }
    }
    
}