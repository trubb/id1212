package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * ServerConnector handles the connection to the specified server
 */
public class ServerConnector {

    private Socket socket;
    private PrintWriter messageToServer;
    private BufferedReader messageFromServer;

    /**
     * Starts a connection to a server over the specified IP address and port
     * @param HOSTNAME the ip address of the server that we will connect to
     * @param PORTNUMBER the port that will be used
     * @throws IOException in case we couldn't connect
     */
    public void connect ( String HOSTNAME, int PORTNUMBER ) throws IOException {
        try {
            socket = new Socket( HOSTNAME, PORTNUMBER );
            messageToServer = new PrintWriter( socket.getOutputStream(), true );
            messageFromServer = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            new Thread( new Listen() ).start();
        } catch (ConnectException e) {
            System.err.println("Unable to find host " + HOSTNAME + " " + PORTNUMBER);
            System.exit(1);
        }
    }

    /**
     * Disconnects from the server
     * @throws IOException in case we couldn't disconnect
     */
    public void disconnect () throws IOException {
         try {
             socket.close();
             socket = null;
         } catch ( IOException e ) {
             System.err.println("Could not disconnect!");
             System.exit(1);
         }
    }

    /**
     * Send a message from the client to the server
     * @param message the message - a command or guess - to be sent to the server
     * @throws IOException in case something goes wrong when trying to send
     */
    public void sendMessage ( String message ) throws IOException {
        messageToServer.println( message );
    }

    /**
     * Listens for messages from the server
     */
    private class Listen implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("S: " + messageFromServer.readLine());
                }
            } catch (Throwable connectionFail) {
                if ( socket == null ) {
                    // do something
                }
            }
        }
    }

}
