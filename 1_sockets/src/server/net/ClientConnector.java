package server.net;

import server.controller.Controller;

import java.io.*;
import java.net.Socket;

public class ClientConnector extends Thread {

    private Socket socket;
    private Controller controller = new Controller();
    private PrintWriter messageToClient;
    private BufferedReader input;

    public ClientConnector ( Socket socket ) {
        super("ClientConnector");
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            messageToClient = new PrintWriter( socket.getOutputStream(), true );
            input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        /**
         * SEND THIS TO CONTROLLER?
         * ALSO NEEDS TO HANDLE GUESSES THAT ARENT CHARS PROBABLY
         * LOW PRIO
         */
        try {
            String instring, outstring;
            outstring = "Hello, you connected to the server";
            messageToClient.println( outstring );

            while ( ( instring = input.readLine() ) != null ) {
                outstring = "You sent: " + instring;
                messageToClient.println( outstring );

                if ( instring.equals("!QUIT") ) {
                    outstring = "BYE!";
                    messageToClient.println( outstring );
                    break;
                } else if ( instring.equals("!PLAY") ) {
                    outstring = "Initializing game..";
                    messageToClient.println( outstring );
                    controller.init();
                } else {
                    controller.makeGuess( instring );
                }
            }

            socket.close();
            System.out.println("Client disconnected");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
