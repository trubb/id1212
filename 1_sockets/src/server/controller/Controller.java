package server.controller;

import server.model.HangManGame;

import java.io.PrintWriter;

public class Controller {

    HangManGame game = new HangManGame();

    public void init() {
        game.init();
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

    public boolean checkEquals() {
        return game.checkEquals();
    }

}
