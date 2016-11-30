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
package edu.eci.arsw.msgbroker;

import edu.eci.arsw.msgbroker.services.GameNotFoundException;
import edu.eci.arsw.msgbroker.services.GameServices;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping("/hangmangames")
public class GamesResourceController {

    @Autowired
    GameServices gameServices;
    @Autowired
    SimpMessagingTemplate msgt;
    
    @RequestMapping(method = RequestMethod.GET)
    public String test(){
        return "Ok";
    }
    
    @RequestMapping(path = "/hiddenwords/{gameid}", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentWord(@PathVariable Integer gameid){
        try {    
            return new ResponseEntity<>(gameServices.getCurrentGuessedWord(gameid),HttpStatus.ACCEPTED);
        } catch (GameNotFoundException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(path = "/hiddenwords/{gameid}/status", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentStatus(@PathVariable Integer gameid){
        try {    
            String respuesta="";
            if(gameServices.isGameFinished(gameid)){
                respuesta="Terminado";
            }else{
                respuesta="En curso";
            }
            return new ResponseEntity<>(respuesta,HttpStatus.ACCEPTED);
        } catch (GameNotFoundException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
        }
    }
    @RequestMapping(path = "/hiddenwords/{gameid}/winner", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentWinner(@PathVariable Integer gameid){
        try {    
            String respuesta=gameServices.getGameWinner(gameid);
            if(respuesta.length()==0){
                respuesta="Ninguno";
            }
            return new ResponseEntity<>(respuesta,HttpStatus.ACCEPTED);
        } catch (GameNotFoundException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(path = "/hiddenwords/{gameid}", method = RequestMethod.PUT)
    public ResponseEntity<?> tryChar(@PathVariable Integer gameid,@RequestBody Character chara){
        try {
            System.out.println("char: "+chara);
            synchronized(gameServices){
                String nString =gameServices.addLetter(gameid,chara);
                msgt.convertAndSend("/topic/wupdate."+gameid,nString);
            }
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GameNotFoundException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
        }
    }
    @RequestMapping(path = "/hiddenwords/{gameid}/{playerid}", method = RequestMethod.PUT)
    public ResponseEntity<?> tryWord(@PathVariable Integer gameid,@PathVariable String playerid,@RequestBody String word){
        try {
            word=word.substring(1,word.length()-1);
            System.out.println("word: " + word);
            boolean end = gameServices.guessWord(playerid, gameid, word);
            if (end) {
                System.out.println("ganó");
                msgt.convertAndSend("/topic/winner." + gameid, playerid);
                msgt.convertAndSend("/topic/wupdate." + gameid, word);
            }

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GameNotFoundException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
        }
    }
    

    
    
    
}
