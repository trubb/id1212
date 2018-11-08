package server.model;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HangManGame {

    HangManDTO dto;

    public void init ( HangManDTO indto ) {
        this.dto = indto;
        dto.setup( selectWord() );

        /** REMOVEME
         *  print dto contents to show what they are
         */
        String s = new String( dto.getWordArray() );
        System.out.println( dto.getWord() + " " + dto.getAllowedAttempts() + " " + s  );
    }

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
        return word;
    }

    /**
     * things for dealing with the rounds of the game
     */

    public void makeGuess ( char c ) {

        if ( dto.getAllowedAttempts() > 0 ) {

            dto.addCharToGuessed( c );
            char[] temp = dto.getWordArray();

            for (int i = 0; i < dto.getWordArray().length; i++) {
                if (temp[i] == c) {
                    dto.updateWordArray( i, c );
                } else {
                    dto.subtractAttempt();
                }
            }
            System.out.println( dto.getWordArrayAsString() );
        }
    }

    public void guessWholeWord ( String input ) {

        if ( dto.getAllowedAttempts() > 0 && input.equals( dto.getWord() ) ) {
            dto.setGameWonState( true );
        } else {
            dto.subtractAttempt();
        }
    }


}
