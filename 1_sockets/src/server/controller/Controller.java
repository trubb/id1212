package server.controller;

import common.Message;
import server.model.HangManGame;

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

    public String getWord() {
        return game.getWord();
    }

    public boolean checkEquals() {
        return game.checkEquals();
    }

    /**
     * NEW: To alleviate the issue of letting the controller scream into the tube
     * that was the messageToClient PrintWriter, we have two helper methods here instead.
     * @return a formatted String with the current game state as per feedback
     */
    public String message(String message) {

        return new Message(
                message,
                game.getAttempts(),
                game.printGuessArray(),
                game.getScore(),
                game.getGuessedChars()
        ).serialize();
    }

    public String message() {

        return new Message(
                "", // in case we do not want to add a message
                game.getAttempts(),
                game.printGuessArray(),
                game.getScore(),
                game.getGuessedChars()
        ).serialize();
    }

}
