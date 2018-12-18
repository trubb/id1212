package server.controller;

import server.model.HangManGame;

import java.io.PrintWriter;

/**
 * The server's controller, handles calls from net to model
 */
public class Controller {

    HangManGame game = new HangManGame();

    public void startGame() {
        game.newGame();
    }

    public void win() {
        game.winRound();
    }

    /**
     * Make a guess of arbitrary length
     * @param input The guess provided by the user, a string of any length
     * @return the content that has been written to the PrintWriter
     */
    public void makeGuess ( String input ) {
        game.makeGuess( input );
    }

    public int getAttempts() {
        return game.getAttempts();
    }

    public String printGuessArray() {
        return game.printGuessArray();
    }

    public int getScore() {
        return game.getScore();
    }

    public String getGuessedChars() {
        return game.getGuessedChars();
    }

    public String getWord() {
        return game.getWord();
    }

    public boolean checkEquals() {
        return game.checkEquals();
    }

    /**
     * NEW: To alleviate the issue of letting the controller scream into the tube
     * that was the messageToClient PrintWriter, we have a helper method here instead.
     * @return a formatted String with the current game state as per feedback
     */
    public String message() {
        return "Attempts " + game.getAttempts() +
                "\n" + game.printGuessArray() +
                "\nScore " + game.getScore() +
                "\n" + game.getGuessedChars();
    }
}
