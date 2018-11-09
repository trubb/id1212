package server.controller;

import server.model.HangManGame;

public class Controller {

    HangManGame game = new HangManGame();

    // TODO add add parser for messages, just feed it message<To/From>Client
    // TODO send data from server in a better way
    // TODO fix stylistic things

    public void newGame() {
        game.newGame();
    }

    public void makeGuess ( String input ) {
        game.makeGuess( input );
    }

    public String printGuessArray () {
        return game.printGuessArray();
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

    public int getScore() {
        return game.getScore();
    }

    public void win() {
        game.winRound();
    }


}
