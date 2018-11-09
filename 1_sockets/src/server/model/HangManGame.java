package server.model;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HangManGame {

    private String word;
    private char[] wordArray;
    private char[] guessArray;
    private int allowedAttempts;
    private int score;
    private Set<Character> guessedChars = new HashSet<>();
    private Set<String> guessedWords = new HashSet<>();
    private Set<Character> wordSet = new HashSet<>();

    public void newGame() {
        clear();
        word = selectWord();
        wordArray = word.toCharArray();
        allowedAttempts = word.length();
        guessArray = new char[word.length()];
        Arrays.fill( guessArray, '_');
        for (int i = 0; i < word.length(); i++) {
            wordSet.add( wordArray[i] );
        }

        /**
         * while testing we print this stuff to the
         * server terminal to see that it works
         */
        System.out.println( "word: " + word );
    }

    /**
     * Selects a random word from the wordfile
     * @return the chosen word as a string
     */
    public String selectWord() {
        String word = "";
        Random random = new Random();
        List<String> words = new ArrayList<>();
        try {
            Scanner wordFile = new Scanner( new File( "src/words.txt" ) );
            while ( wordFile.hasNextLine() ) {
                words.add( wordFile.nextLine().toLowerCase() );
            }
            wordFile.close();

            int select = random.nextInt( words.size() );
            word = words.get( select );

        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( word.length() == 1){
            word = selectWord();
            return word;
        } else {
            return word;
        }
    }

    /**
     * things for dealing with the rounds of the game
     */

    /**
     * Helper method to make guesses
     * @param input a string of arbitrary length
     */
    public void makeGuess ( String input ) {
        if ( allowedAttempts > 0 ) {
            if (input.length() == 1) {
                char[] ca = input.toCharArray();
                guessChar(ca[0]);
            } else {
                guessWholeWord(input);
            }
        }
    }

    /**
     * Make a guess based on a character
     * @param c the character that is guessed
     */
    private void guessChar ( char c ) {
        if ( !guessedChars.contains( c ) ) {
            guessedChars.add( c );
            if (wordSet.contains( c )) {
                for (int i = 0; i < word.length(); i++) {
                    if ( wordArray[i] == c ) {
                        guessArray[i] = wordArray[i];
                    }
                }
            } else {
                allowedAttempts--;
            }
            System.out.println( allowedAttempts + " " + c );
        }
    }

    /**
     * Make a guess based on a string
     * @param input the string that is guessed
     */
    private void guessWholeWord ( String input ) {
        if ( !guessedWords.contains( input ) ) {
            guessedWords.add( input );
            if ( word.equals( input ) ) {
                guessArray = word.toCharArray();
            } else {
                allowedAttempts--;
            }
            System.out.println( allowedAttempts + " " + input );
        }
    }

    /**
     * Check if the client has guessed all letters of the word
     * @return a boolean, true if the guess array is the same as the word
     */
    public boolean checkEquals() {
        return Arrays.equals(wordArray, guessArray);
    }

    public int getAttempts() {
        return allowedAttempts;
    }

    public String getWord() {
        return word;
    }

    public int getScore() {
        return score;
    }

    public void winRound() {
        score++;
        newGame();
    }

    private void clear() {
        word = "";
        wordArray = new char[0];
        guessArray = new char[0];
        allowedAttempts = 0;
        guessedChars.clear();
        guessedWords.clear();
        wordSet.clear();
    }

    public String printGuessArray () {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guessArray.length; i++) {
            sb.append( guessArray[i] );
        }
        return sb.toString();
    }

}
