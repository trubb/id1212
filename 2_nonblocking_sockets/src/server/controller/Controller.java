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

    /**
     * Set up the Controller so that we use the correct channels sockets and buffer
     * @param socketChannel the ServerSocketChannel that was created by the Server
     * @param socket the socket that was created by the ClientHandler
     * @param buffer the buffer that was created by the ClientHandler
     */
    public Controller ( SocketChannel socketChannel, Socket socket, ByteBuffer buffer ) {
        this.socketChannel = socketChannel;
        this.socket = socket;
        this.buffer = buffer;
    }

    // TODO - should be in clienthandler???
    // TODO - should not be in this shitty "just works"-format either??? See previous solution

    /**
     * Start the game and parse the input from the client
     * @param start input from the client TODO - rename this parameter
     */
    public void run (String start) {
        System.out.println("provided: " + start);

        if ( start.equals("play") ) {
            init();
            printline("New game started");

            String input = "";
            while ( !input.equals("quit") )  {
                input = readMessage( buffer );
                printline("Word is ");
                printline("Remaining attempts ");
                printline( Integer.toString( game.getAttempts() ) );

                game.makeGuess( input );

                if (game.checkEquals()) {
                    printline("Round won");
                } else if ( game.getAttempts() == 0 ) {
                    printline("You used upp all attempts");
                    printline("The chosen word was " + game.getWord());
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
