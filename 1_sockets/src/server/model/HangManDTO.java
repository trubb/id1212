package server.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HangManDTO implements Serializable {

    private String word;
    private char[] wordArray;
    private int allowedAttempts;
    Set<Character> guessedChars = new HashSet<>();
    private boolean gameWon;

    public void setup ( String word ) {
        setWord( word );
        wordToCharArray( word );
        setAllowedAttempts( word );
        setGameWonState( false );
    }

    /**
     * Setters live below here
     * @param word the word chosen by the hangman game
     */
    private void setWord ( String word ) {
        this.word = word;
    }

    private void wordToCharArray ( String word ) {
        this.wordArray = word.toCharArray();
        Arrays.fill( this.wordArray, '_' );
    }

    private void setAllowedAttempts ( String word ) {
        this.allowedAttempts = word.length();
    }

    public void subtractAttempt () {
        this.allowedAttempts--;
    }

    public void setGameWonState ( boolean bool ) {
        this.gameWon = bool;
    }

    public void updateWordArray (int index, char c ) {
        this.wordArray[ index ] = c;
    }

    public void addCharToGuessed ( char c ) {
        if ( !guessedChars.contains( c ) ) {
            this.guessedChars.add( c );
        }
    }


    /**
     * Getters live below here
     * @return the sought after variable
     */

    public String getWord() {
        return this.word;
    }

    public char[] getWordArray() {
        return this.wordArray;
    }

    public int getAllowedAttempts() {
        return this.allowedAttempts;
    }
}
