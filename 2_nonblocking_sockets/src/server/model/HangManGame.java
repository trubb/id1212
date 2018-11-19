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

    private String word;
    private char[] wordArray;
    private char[] guessArray;
    private int remainingAttempts;
    private int score;
    private Set<Character> guessedChars = new HashSet<>();
    private Set<String> guessedWords = new HashSet<>();
    private Set<Character> wordSet = new HashSet<>();


    private String chosenWord;
    private String currentState;

    public HangManGame() {
        this.score = 0;
    }

    public String startRound() {
        chosenWord = selectWord().toUpperCase();
        remainingAttempts = chosenWord.length();
        currentState = chosenWord.replaceAll("[a-zA-Z]", "_");  // replace all letters with _

        return buildMessage();
    }

    private String buildMessage() {
        return "You have to guess: " + "  -  [remaining attempts: " + remainingAttempts + "; score: " + score + "]";
    }
    // TODO - Possible fail place

    public String validateGuess (String guess) {
        guess.toUpperCase();
        if (guess.length() == 1) {
            validateLetter(guess);
        } else {
            validateWord(guess);
        }
        return buildMessage();
    }

    /**
     * Consider doing this with just a char instead...
     * @param letter
     */
    private void validateLetter (String letter) {
        if ( !chosenWord.contains(letter) ) {
            if (remainingAttempts <= 1) {
                if (score > 0) score--;
                currentState = chosenWord;
                chosenWord = null;
                return;
            }
            remainingAttempts--;
            return;
        }

        char[] chosenCharArray = chosenWord.toCharArray();
        char[] currentCharArray = currentState.toCharArray();
        char letterChar = letter.charAt(0);

        for (int i = 0; i < chosenCharArray.length; i++) {
            if (chosenCharArray[i] == letterChar) {
                currentCharArray[i] = letterChar;
            }
        }

        currentState = String.valueOf(currentCharArray);
    }

    private void validateWord (String word) {
        if ( word.equals(chosenWord) ) {
            score++;
            currentState = word;
            chosenWord = null;
            return;
        }
        if (remainingAttempts <= 1) {
            if (score > 0) score--;
            currentState = chosenWord;
            chosenWord = null;
            return;
        }
        remainingAttempts--;
    }

    public String getChosenWord() {
        return chosenWord;
    }

    /**
     * Start a new game round
     */
    public void newGame() {
        clear();
        word = selectWord();
        wordArray = word.toCharArray();
        remainingAttempts = word.length();
        guessArray = new char[word.length()];
        Arrays.fill( guessArray, '_');
        for (int i = 0; i < word.length(); i++) {
            wordSet.add( wordArray[i] );
        }

        /**
         * We print the chosen word to the server's Terminal
         * to see that it works (and for cheating purposes)
         */
        System.out.println( "word: " + word );
    }

    /**
     * Selects a random word from words.txt
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

        /**
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

    /**
     * Quick and dirty clearing of values between rounds
     * to make sure that there are no lingering values
     */
    private void clear() {
        word = "";
        wordArray = new char[0];
        guessArray = new char[0];
        remainingAttempts = 0;
        guessedChars.clear();
        guessedWords.clear();
        wordSet.clear();
    }

    /**
     * Helper method to allow the client to make
     * guesses of possibly arbitrary length
     * @param input a string of arbitrary length
     */
    public void makeGuess ( String input ) {
        if ( remainingAttempts > 0 ) {
            if (input.length() == 1) {
                char[] ca = input.toCharArray();
                guessChar(ca[0]);
            } else {
                guessWholeWord(input);
            }
        }
    }

    /**
     * Guess a character
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
                remainingAttempts--;
            }
            System.out.println( remainingAttempts + " " + c );
        }
    }

    /**
     * Guess a word
     * @param input the string that is guessed
     */
    private void guessWholeWord ( String input ) {
        if ( !guessedWords.contains( input ) ) {
            guessedWords.add( input );
            if ( word.equals( input ) ) {
                guessArray = word.toCharArray();
            } else {
                remainingAttempts--;
            }
            System.out.println( remainingAttempts + " " + input );
        }
    }

    /**
     * Check if the client has guessed all letters of the word
     * @return a boolean, true if the guess array is the same as the word
     */
    public boolean checkEquals() {
        return Arrays.equals(wordArray, guessArray);
    }

    /**
     * @return the number of attempts left for the round
     */
    public int getAttempts() {
        System.out.println(remainingAttempts);
        return remainingAttempts;
    }

    /**
     * @return the word that has been selected for the round
     */
    public String getWord() {
        return word;
    }

    /**
     * @return the current score, persistent between rounds
     */
    public int getScore() {
        return score;
    }

    /**
     * Helper function for starting a new round
     */
    public void winRound() {
        score++;
        newGame();
    }

    /**
     * Print the array of obfuscated letters to the client
     * Letters are deobfuscated as they are correctly guessed
     * @return the char array as a string
     */
    public String printGuessArray () {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guessArray.length; i++) {
            sb.append( guessArray[i] );
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * Helper function for printing the set of guessed chars
     * The set is used to keep track of what characters have
     * been guessed already, and as we make sure that no char
     * can be guessed twice it is nice to send this back to
     * @return the set of guessed letters as a string
     */
    public String getGuessedChars() {
        return guessedChars.toString();
    }

}
