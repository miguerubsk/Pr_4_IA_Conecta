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
 * @author Miguel González García
 * @version 1.4 Departamento de Informática. Universidad de Jáen
 *
 */
public abstract class Player {

    // Devuelve una columna al azar que no esté completa
    protected int getRandomColumn(Grid tablero) {
        int posicion;

        //Buscar columna en la que se pueda poner la ficha
        do {
            posicion = (int) (Math.random() * tablero.getColumnas());
        } while (tablero.fullColumn(posicion));

        return posicion;
    } // getRandomColumn

    // Método abstracto para colocar una ficha en el tablero
    /**
     *
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    public abstract int jugada(Grid tablero, int conecta);

} // Player
