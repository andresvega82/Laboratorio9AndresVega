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
//NOTA: los comentarios no llevan acentos para evitar conflictos de codificacion
package edu.eci.arsw.msgbroker.services;

import edu.eci.arsw.msgbroker.model.HangmanGame;
import java.util.Random;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
@Service
public class GameServices {

    @Autowired
    GameStatePersistence persistence;

    private final String[] words;
    private final Random random;

    public GameServices() {
        random=new Random(System.currentTimeMillis());
        words=new String[]{
            "happiness",
            "foot",
            "player",
            "winner"
        };
    }
    
    /**
     * Crea un nuevo juego, con una palabra creada al azar
     * @param gameid
     * @throws GameCreationException 
     */
    public void createGame(int gameid) throws GameCreationException{
       persistence.createGame(gameid, words[random.nextInt(words.length+1)]);
    }

    /**
     * Selecciona una nueva letra para la palabra secreta del juego identificado
     * con 'gameid'.
     * @param gameid identificador del juego
     * @param letter letra elegida por el usuario
     * @return la nueva palabra, mostrando la letra elegida. Por ejemplo, si 
     * la palabra es 'felicidad', y en el primer intento del juego se elige
     * la letra 'd', el metodo retornara:  '______d_d'. Si en un siguiente intento
     * se elige la letra 'i', el metodo retornara: '___i_id_d'
     * @throws GameNotFoundException si el identificador dado no corresponde
     * a una partida existente.
     */
    public String addLetter(int gameid,char letter) throws GameNotFoundException{
        persistence.addLetter(gameid, letter);
        return persistence.getGame(gameid).getCurrentGuessedWord();
    }
    
    /**
     * Retorna la palabra que esta siendo adivinada actualmente en el juego
     * identificado con 'gameid', en su estado actual (es decir, ocultando 
     * las letras aun no descubiertas)
     * @param gameid
     * @return la palabra en su estado actual, ocultando los caracteres
     * no descubiertos.
     * @throws GameNotFoundException si el identificador dado no corresponde
     * a una partida existente.
     */
    public String getCurrentGuessedWord(int gameid) throws GameNotFoundException{
        return persistence.getGame(gameid).getCurrentGuessedWord();
    }
    
    /**
     * Permite realizar un intento de adivinar la palabra secreta.
     * @param playerName el nombre del jugador que realiza el intento.
     * @param gameid el identificador del juego de la partida
     * @param word la palabra que el usuario su     
     * @return true si la palabra fue adivinada, false d.l.c.
     * @throws GameNotFoundException si el identificador dado no corresponde
     * a una partida existente.
     */
    public boolean guessWord(String playerName,int gameid,String word) throws GameNotFoundException{
        return persistence.checkWordAndUpdateHangman(gameid, playerName, word);        
    }
    
    /**
     * Indica si el juego del identificador dado ya ha sido finalizado
     * @param gameid
     * @return true si el juego termino, false d.l.c.
     * @throws GameNotFoundException si el identificador dado no corresponde
     * a una partida existente.
     */
    public boolean isGameFinished(int gameid) throws GameNotFoundException{
        return persistence.getGame(gameid).gameFinished();
    }
    
    /**
     * Consulta el nombre del jugador declarado como ganador
     * @pre isGameFinished==true
     * @param gameid
     * @return el nombre del jugador ganador.
     * @throws GameNotFoundException si el identificador dado no corresponde
     * a una partida existente.
     */
    public String getGameWinner(int gameid) throws GameNotFoundException{
        return persistence.getGame(gameid).getWinnerName();
    }
    
  
    
}
