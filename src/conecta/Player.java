/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * Clase abstracta Player para representar al jugador CPU
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
