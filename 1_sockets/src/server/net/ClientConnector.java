package server.net;

import server.controller.Controller;

import java.io.*;
import java.net.Socket;

public class ClientConnector extends Thread {

    private Socket socket;
    private Controller controller = new Controller();
    private PrintWriter messageToClient;
    private BufferedReader messageFromClient;

    public ClientConnector ( Socket socket ) {
        super("ClientConnector");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            messageToClient = new PrintWriter( socket.getOutputStream(), true );
            messageFromClient = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        while (true) {
            try {
                String userInput;

                while ( ( userInput = messageFromClient.readLine() ) != null ) {

                    if ( userInput.equals("!PLAY") ) {
                        controller.newGame();
                        messageToClient.println( controller.getAttempts() );
                        messageToClient.println( controller.printGuessArray() );
                        messageToClient.println( controller.getScore() );

                        while ( !userInput.equals("!QUIT") ) {
                            userInput = messageFromClient.readLine();
                            controller.makeGuess( userInput );
                            messageToClient.println( controller.getAttempts() );
                            messageToClient.println( controller.printGuessArray() );
                            messageToClient.println( controller.getScore() );

                            if ( controller.checkEquals() ) {
                                controller.win();
                                messageToClient.println( controller.getAttempts() );
                                messageToClient.println( controller.printGuessArray() );
                                messageToClient.println( controller.getScore() );

                            } else if ( controller.getAttempts() == 0) {
                                messageToClient.println( controller.getWord() );
                                controller.newGame();
                                messageToClient.println( controller.getAttempts() );
                                messageToClient.println( controller.printGuessArray() );
                                messageToClient.println( controller.getScore() );

                            }
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (socket != null) {
                    socket.close();
                    System.out.println("Client disconnected.");
                    break;
                }
            } catch (IOException ioEx) {
                System.out.println("Unable to disconnect!");
            }

        }

    }

}
