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
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
public class InMemoryGamesStatePersistence implements GameStatePersistence{
    
    private final ConcurrentHashMap<Integer,HangmanGame> gamesState;
    
    
    
    public InMemoryGamesStatePersistence(){
        gamesState=new ConcurrentHashMap<>();
        preloadGames();
    }
    
    @Override
    public void createGame(int id,String word) throws GameCreationException{
        if (gamesState.containsKey(id)){
            throw new GameCreationException("The game "+id+" already exist.");
        }
        else{
            gamesState.put(id, new HangmanGame(word));
        }
        
    }
    
    @Override
    public HangmanGame getGame(int gameid) throws GameNotFoundException{
        if (!gamesState.containsKey(gameid)){
            throw new GameNotFoundException("The game "+gameid+" doesnt exist.");
        }
        else{
            return gamesState.get(gameid);
        }
        
    }
    
    private void preloadGames(){
        HangmanGame hg=new HangmanGame("happiness");
        hg.addLetter('h');
        hg.addLetter('e');
        gamesState.put(1, hg);
        
        gamesState.put(2, new HangmanGame("foot"));
        gamesState.put(3, new HangmanGame("player"));
        gamesState.put(4, new HangmanGame("winner"));        
    }

    @Override
    public void addLetter(int gameid, char c) throws GameNotFoundException {
        if (!gamesState.containsKey(gameid)){
            throw new GameNotFoundException("The game "+gameid+" doesnt exist.");
        }
        else{            
            gamesState.get(gameid).addLetter(c);
        }        
    }

    @Override
    public boolean checkWordAndUpdateHangman(int gameid, String player,String word) throws GameNotFoundException {
        if (!gamesState.containsKey(gameid)){
            throw new GameNotFoundException("The game "+gameid+" doesnt exist.");
        }
        else{
            return gamesState.get(gameid).guessWord(player, word);
        }        
        
    }
    
}
