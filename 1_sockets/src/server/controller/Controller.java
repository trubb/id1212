package server.controller;

import server.model.HangManGame;

import java.io.PrintWriter;

/**
 * The server's controller, handles calls from net to model
 */
public class Controller {

    HangManGame game = new HangManGame();

    /**
     * Starts a new game round
     * @param messageToClient PrintWriter endpoint for sending messages to the client
     * @return the content that has been written to the PrintWriter
     */
    public String startGame ( PrintWriter messageToClient ) {
        game.newGame();
        messages( messageToClient );
        return messageToClient.toString();
    }

    /**
     * Used to declare the client as a winner of a round
     * @param messageToClient PrintWriter endpoint for sending messages to the client
     * @return the content that has been written to the PrintWriter
     */
    public String win ( PrintWriter messageToClient ) {
        game.winRound();
        messages( messageToClient );
        return messageToClient.toString();
    }

    /**
     * Used to declare the client as a loser of a round
     * @param messageToclient PrintWriter endpoint for sending messages to the client
     * @return the content that has been written to the PrintWriter
     */
    public String lose ( PrintWriter messageToclient ) {
        messageToclient.println( getWord() );
        startGame( messageToclient );
        return messageToclient.toString();
    }

    /**
     * Used to provide the game instance with what it needs to make a guess
     * @param input The guess provided by the user, a string of any length
     * @param messageToClient PrintWriter endpoint for sending messages to the client
     * @return the content that has been written to the PrintWriter
     */
    public String makeGuess ( String input, PrintWriter messageToClient ) {
        game.makeGuess( input );
        messages( messageToClient );
        return messageToClient.toString();
    }

    /**
     * Helper for gathering all information about a round for sending to the client.
     * @param messageToClient PrintWriter endpoint for sending messages to the client
     * @return Attempts left, the blanked out word, current score, and characters that the user have guessed
     */
    /* TODO - move messaging to ClientConnector, pass connector as arg to controller when creating it to alleviate */
    private String messages ( PrintWriter messageToClient ) {
        messageToClient.println( game.getAttempts() );
        messageToClient.println( game.printGuessArray() );
        messageToClient.println( game.getScore() );
        messageToClient.println( game.getGuessedChars() );
        return messageToClient.toString();
    }

    /**
     * @return the number of attempts that the user has left in a round
     */
    public int getAttempts() {
        return game.getAttempts();
    }

    /**
     * @return the word that has been chosen for a specific round
     */
    public String getWord() {
        return game.getWord();
    }

    /**
     * Check if the user has managed to guess the word letter by letter
     * @return true if they managed to guess correctly, false if not
     */
    public boolean checkEquals() {
        return game.checkEquals();
    }


}
