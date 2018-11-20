package server.model;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The hangman game implementation.
 * At the processConnections of a round a new word is selected from the file words.txt.
 * The client receives information about how long the chosen word is, what
 * letters they have guessed, and what their current score is.
 */
public class HangManGame {

    private int remainingAttempts;  // how many attempts the user has (left)
    private int score;  // the user's score
    private String word;    // the word that has been selected for a given round
    private String obfuscatedWord;  // the word but overwritten with '_', filled with correctly guessed characters

    /**
     * Initialize score to 0 when game is created
     */
    public HangManGame() {
        this.score = 0;
    }

    /**
     * Start a new round
     * Selects a word and sets up <code>remainingAttempts</code> and
     * <code>obfuscatedWord</code> to reflect the selected word.
     * @return a String containing information about the current game state
     */
    public String startRound() {
        word = selectWord().toUpperCase();  // pick a word, set it to be all uppercase
        remainingAttempts = word.length();  // the user gets as many attempts as there are letters in word
        obfuscatedWord = word.replaceAll("[a-zA-Z]", "_");  // replace all letters with _

        System.out.println(word); // for cheating purposes
        return roundMessage();  // return current game state
    }

    /**
     * Helper for constructing a message containing the current game state
     * @return the state of the obfuscated word, attempts left, and score
     */
    private String roundMessage() {
        return "Word: " + obfuscatedWord +
                "\nRemaining attempts: " + remainingAttempts +
                "\nScore: " + score;
    }

    /**
     * Parser for guess type
     * @param guess a String of length 1 - n
     * @return current game state
     */
    public String guessParser (String guess) {
        guess.toUpperCase();
        if (guess.length() == 1) {
            guessLetter(guess);
        } else {
            guessWord(guess);
        }
        return roundMessage();
    }

    /**
     * Make a guess based on a provided letter
     * @param letter the letter to be checked against the selected word
     */
    private void guessLetter (String letter) {
        if ( !word.contains(letter) ) { // if the word does not contain the provided letter
            if (remainingAttempts <= 1) {   // if remaining attempts are 1 or below right now (ie will be 0 now)
                obfuscatedWord = word;  // deobfuscate the word
                word = null;    // reset the chosen word
                return;
            }
            remainingAttempts--;    // decrement remaining attempts, bad guess
            return; // exit
        }

        // setup char arrays
        char[] wordCA = word.toCharArray();
        char[] obfuscatedCA = obfuscatedWord.toCharArray();
        char letterChar = letter.charAt(0);

        // each letter in the word matching the provided letter is deobfuscated
        for (int i = 0; i < wordCA.length; i++) {
            if (wordCA[i] == letterChar) {
                obfuscatedCA[i] = letterChar;
            }
        }
        // set the obfuscated word to be its partially deobfuscated twin
        obfuscatedWord = String.valueOf(obfuscatedCA);
    }

    /**
     * Make a guess based on a provided word
     * @param word the word to be checked against the selected word
     */
    private void guessWord (String word) {
        if ( word.equals(this.word) ) { // if the word is correctly guessed, increment score and reset
            score++;
            obfuscatedWord = word;
            this.word = null;
            return;
        }
        if (remainingAttempts <= 1) {   // if incorrectly guessed and no guesses left: reveal the word and reset
            obfuscatedWord = this.word;
            this.word = null;
            return;
        }
        remainingAttempts--;    // if incorrectly guessed decrement attempts
    }

    /**
     * Helper for checking input against the selected word
     * @return the selected word
     */
    public String getWord() {
        return word;
    }

    /**
     * Selects a random word from words.txt
     * @return the chosen word as a string
     */
    private String selectWord() {
        String word = "";
        Random random = new Random();
        List<String> words = new ArrayList<>();
        try {
            Scanner wordFile = new Scanner( new File( "src/words.txt" ) );
            while ( wordFile.hasNextLine() ) {
                words.add( wordFile.nextLine().toUpperCase() );
            }
            wordFile.close();

            int select = random.nextInt( words.size() );
            word = words.get( select );

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * As 1-letter words are a 1/26 chance of getting right
         * we discard these by redoing the process if we get one
         */
        if ( word.length() == 1){
            word = selectWord();
            return word;
        } else {
            return word;
        }
    }

}
