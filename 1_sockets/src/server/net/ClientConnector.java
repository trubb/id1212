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
                String instring, outstring;

                while ((instring = messageFromClient.readLine()) != null) {

                    if (instring.equals("!PLAY")) {
                        outstring = "Game started, you may now guess individual letters or a whole word";
                        messageToClient.println(outstring);
                        controller.init();
                        messageToClient.println( controller.printGuessArray() );
                    } else {
                        controller.makeGuess(instring);
                        messageToClient.println( controller.getAttempts() + controller.printGuessArray() );
                        controller.checkEquals();
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
