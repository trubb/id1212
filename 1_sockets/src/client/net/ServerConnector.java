package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class ServerConnector {

    private Socket socket;
    private PrintWriter messageToServer;
    private BufferedReader messageFromServer;

    public void connect ( String HOSTNAME, int PORTNUMBER ) throws IOException {
        try {
            socket = new Socket( HOSTNAME, PORTNUMBER );
            messageToServer = new PrintWriter( socket.getOutputStream(), true );
            messageFromServer = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            new Thread( new Listen() ).start();
        } catch (ConnectException e) {
            System.err.println("ServCon: Unable to find host " + HOSTNAME + " " + PORTNUMBER);
            System.exit(1);
        }
    }

    public void disconnect () throws IOException {
         try {
             System.out.println("ServCon: Connection closing!");
             socket.close();
             socket = null;
         } catch ( IOException e ) {
             System.out.println("ServCon: Could not disconnect!");
             System.exit(1);
         }
    }

    public void sendMessage ( String message ) throws IOException {
        messageToServer.println( message );
    }

    private class Listen implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println( "S: " + messageFromServer.readLine() );
                }
            } catch (Throwable connectionFail) {
                if ( socket == null ){
                    System.out.println("ServCon: Lost connection to server");
                }
            }
        }
    }

}
