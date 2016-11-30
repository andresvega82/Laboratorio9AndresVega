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
package edu.eci.arsw.msgbroker.model;

/**
 *
 * @author hcadavid
 */
public class HangmanGame {
    
    private final String word;
    private final char[] guessedWord;
    private String winner="";
    private boolean gameFinished=false;

    public HangmanGame(String word, String gw,String winner, boolean finished) {
        this.word = word;        
        this.guessedWord = gw.toCharArray();
        this.winner=winner;
        this.gameFinished=finished;
    }
    
    public HangmanGame(String word) {
        this.word=word.toLowerCase();
        guessedWord=new char[word.length()];
        for (int i=0;i<word.length();i++){
            guessedWord[i]='_';
        }                
    }
    
    /**
     * @pre gameFinished==false
     * @param l new letter
     * @return the secret word with all the characters 'l' revealed
     */
    public String addLetter(char l){                
        for (int i=0;i<word.length();i++){
            if (word.charAt(i)==l){
                guessedWord[i]=l;
            }            
        }    
        return new String(guessedWord);
    }
    
    public synchronized boolean guessWord(String playerName,String s){
        System.out.println(s.toLowerCase()+" vs "+word+" "+s.toLowerCase().equals(word));
        if (s.toLowerCase().equals(word)){
            winner=playerName;
            gameFinished=true;
            return true;
        }
        return false;
    }
    
    public boolean gameFinished(){
        return gameFinished;
    }
    
    /**
     * @pre gameFinished=true;
     * @return winner's name
     */
    public String getWinnerName(){
        return winner;
    }
    
    public String getCurrentGuessedWord(){
        if(!(gameFinished))
            return new String(guessedWord);
        else return word;
    }   
    
    public String getWord(){
        return word;
    }

    @Override
    public String toString() {
        return "HangmanGame{" + "word=" + word + ", guessedWord=" + new String(guessedWord) + ", winner=" + winner + ", gameFinished=" + gameFinished + '}';
    }
    
    
}
