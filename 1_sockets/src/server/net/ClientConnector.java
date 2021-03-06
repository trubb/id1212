package server.net;

import server.controller.Controller;

import java.io.*;
import java.net.Socket;

/**
 * CilentConnector handles the connection to a client
 * and parses the messages sent from the client.
 */
public class ClientConnector extends Thread {
    private Socket socket;
    private Controller controller = new Controller();
    private PrintWriter messageToClient;
    private BufferedReader messageFromClient;

    /**
     * Creates an instance of the ClientConnector using the provided socket
     * @param socket the client to which this instance shall connect
     */
    public ClientConnector ( Socket socket ) {
        this.socket = socket;
    }

    /**
     * Run method that handles communication to and from the client
     */
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
                        controller.startGame();
                        messageToClient.println( controller.message() );

                        while ( !userInput.equals("!QUIT") ) {
                            userInput = messageFromClient.readLine();
                            controller.makeGuess( userInput );
                            messageToClient.println( controller.message() );

                            if ( controller.checkEquals() ) {
                                messageToClient.println( controller.message(
                                        "\nYou made a correct guess\n" +
                                       "The word was: " + controller.getWord() + "\n" ) );
                                controller.win();
                                messageToClient.println( controller.message() );

                            } else if ( controller.getAttempts() == 0) {
                                messageToClient.println( controller.message(
                                        "\nYou do not have any attempts left\n" +
                                        "The word was: " + controller.getWord() + "\n" ) );
                                controller.startGame();
                                messageToClient.println( controller.message() );
                            }
                        }

                    }
                }
            } catch (IOException e) {
//                e.printStackTrace();
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
