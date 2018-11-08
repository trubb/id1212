package server.controller;

import server.model.HangManDTO;
import server.model.HangManGame;

public class Controller {

    HangManGame game = new HangManGame();
    HangManDTO dto = new HangManDTO();

    public void init() {
        game.init( dto );
    }

    public void makeGuess ( String input ) {

        if ( input.length() == 1 ) {
            char[] ca = input.toCharArray();
            game.makeGuess(ca[0]);
        } else {
            game.guessWholeWord( input );
        }
    }



}
