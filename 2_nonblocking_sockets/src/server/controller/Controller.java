package server.controller;

import server.model.HangManGame;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Controller {


    private final SocketChannel socketChannel;
    private final Socket socket;
    private final ByteBuffer buffer;
    HangManGame game = new HangManGame();


    public Controller ( SocketChannel socketChannel, Socket socket, ByteBuffer buffer ) {
        this.socketChannel = socketChannel;
        this.socket = socket;
        this.buffer = buffer;
    }

    // should be in clienthandler???
    public void run (String start) {
        System.out.println("provided: " + start);

        if ( start.equals("!PLAY") ) {
            init();
            printline("New game started");

            String input = "";
            while ( !input.equals("!QUIT") )  {
                input = readMessage( buffer );
                printline("Word is ");
                printline("Remaining attempts ");
                printline( Integer.toString( game.getAttempts() ) );

                game.makeGuess( input );

                if (game.checkEquals()) {

                }



            }



        }
    }

    private String readMessage (ByteBuffer buffer) {
        buffer.flip();  // gotta flip to read
        byte[] bytes = new byte[ buffer.remaining() ];
        buffer.get(bytes);
        return new String(bytes);
    }

    public void printline (String messageToUser) {
        ByteBuffer buffer = ByteBuffer.wrap( ( messageToUser ).getBytes() );
        try {
            socketChannel.write( buffer );
        } catch (IOException e) {
            System.err.println("message sending failed");
            e.printStackTrace();
        }
    }

    public void init() {
        game.newGame();
    }


}
