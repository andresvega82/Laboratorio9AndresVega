/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.msgbroker.services;

import edu.eci.arsw.msgbroker.model.HangmanGame;
import edu.edi.arsw.msgbroker.util.JedisUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 *
 * @author 2099340
 */
@Service
public class REDISGamePersistence implements GameStatePersistence{
    @Autowired
    GameStatePersistence persistence;
    
    @Override
    public HangmanGame getGame(int gameid) {
        Jedis jedis = JedisUtil.getPool().getResource();        
        Map<String, String> partida = jedis.hgetAll("partida:" + gameid);        
        HangmanGame resp = new HangmanGame(partida.get("palabra"), partida.get("adivinado"), partida.get("ganador"), partida.get("estado").equals("true")?true:false);
        jedis.close(); 
    return resp;
    }
    public synchronized void addLetter(int gameid, char c) throws GameNotFoundException{
        Jedis jedis = JedisUtil.getPool().getResource(); 
        try {
            Map<String, String> partida = jedis.hgetAll("partida:" + gameid);        
            HangmanGame juegoActual = new HangmanGame(partida.get("palabra"), partida.get("adivinado"), partida.get("ganador"), partida.get("estado").equals("true")?true:false);
            juegoActual.addLetter(c);

            Map<String, String> userProperties = new HashMap<String, String>();
            userProperties.put("partida", ""+gameid);
            userProperties.put("palabra", juegoActual.getWord());
            userProperties.put("adivinado", juegoActual.getCurrentGuessedWord());
            userProperties.put("ganador", juegoActual.getWinnerName());
            userProperties.put("estado", ""+juegoActual.gameFinished());
            jedis.hmset("partida:" + gameid, userProperties);
            jedis.close();
            
        } catch (Exception ex) {
            Logger.getLogger(REDISGamePersistence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public  synchronized boolean checkWordAndUpdateHangman(int gameid, String player,String word) throws GameNotFoundException {
        Jedis jedis = JedisUtil.getPool().getResource(); 
        boolean resp = false;
        jedis.watch("partida:"+gameid);
        Transaction tr = jedis.multi();
        
        try {
            Response<Map<String, String>> partidaActual = tr.hgetAll("partida:" + gameid); 
            Map<String, String> partida = partidaActual.get();
            HangmanGame juegoActual = new HangmanGame(partida.get("palabra"), partida.get("adivinado"), partida.get("ganador"), partida.get("estado").equals("true")?true:false);
            if(!juegoActual.gameFinished()){
                if(juegoActual.guessWord(player, word)){
                    resp = true;
                    Map<String, String> userProperties = new HashMap<String, String>();
                    userProperties.put("partida", "" + gameid);
                    userProperties.put("palabra", juegoActual.getWord());
                    userProperties.put("adivinado", juegoActual.getWord());
                    userProperties.put("ganador", juegoActual.getWinnerName());
                    userProperties.put("estado", ""+juegoActual.gameFinished());
                    tr.hmset("partida:" + gameid, userProperties);    
                    tr.exec();
                    jedis.close();
                }
            }else{
                resp = false;
            }
        } catch (Exception ex) {
            Logger.getLogger(REDISGamePersistence.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resp;
    
    }

    @Override
    public void createGame(int id, String word) throws GameCreationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
