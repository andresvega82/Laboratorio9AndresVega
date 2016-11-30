/*
 * Copyright (C) 2016 Pivotal Software, Inc.
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
package edu.eci.arsw.msgbroker.services;

import edu.eci.arsw.msgbroker.model.HangmanGame;

/**
 *
 * @author hcadavid
 */
public interface GameStatePersistence {
    
    /**
     * Crea y hace persitente el estado de un nuevo juego, con la palabra
     * indicada
     * @param id identificador del juego
     * @param word palabra del juego
     * @throws GameCreationException si se está usando un identificador
     * que no esta disponible
     */
    public void createGame(int id,String word) throws GameCreationException;
    
    /**
     * Consultar un juego, dado su identificador
     * @param gameid el identificador del juego
     * @return una representacion en objetos del juego del identificador dado
     * @throws GameNotFoundException si no hay un juego con el identificador dado
     */
    public HangmanGame getGame(int gameid) throws GameNotFoundException;
    
    /**
     * Agrega una letra al juego actual.
     * @param gameid el identificador del juego
     * @param c la letra a ser agregada
     * @throws GameNotFoundException si no hay un juego con el identificador dado
     */
    public void addLetter(int gameid, char c) throws GameNotFoundException;
    
    /**
     * Realiza un intento de adivinar la palabra del juego indicado
     * @param gameid identificador del juego
     * @param player nombre del jugador que realiza el intento
     * @param word la palabra adivinada
     * @return true si la palabra es la correcta
     * @pos si se adivino correctamente, se debe cambiar el estado del juego
     *      a finalizado, y se debe asignar el jugador indicado como 
     *      ganador
     * @throws GameNotFoundException si no hay un juego con el identificador dado
     */
    public boolean checkWordAndUpdateHangman(int gameid, String player,String word) throws GameNotFoundException ;
    
    
}
