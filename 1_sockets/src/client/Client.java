package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static String HOSTNAME = "130.229.159.12";
    private static final int PORTNUMBER = 48921;

    public static void main(String[] args) throws IOException {

        Controller controller = new Controller();
        controller.connect( HOSTNAME, PORTNUMBER );
/*
        try (
            Socket socket = new Socket( HOSTNAME, PORTNUMBER );
            PrintWriter messageToServer = new PrintWriter( socket.getOutputStream(), true );
            BufferedReader messageFromServer = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        ) {
            BufferedReader stdIn = new BufferedReader( new InputStreamReader( System.in ) );
            String fromServer;
            String fromUser;

            while ( ( fromServer = messageFromServer.readLine() ) != null ) {

                System.out.println("S: " + fromServer);
                if ( fromServer.equals("BYE!") ) {
                    break;
                }

                fromUser = stdIn.readLine();

                if ( fromUser != null ) {
                    messageToServer.println(fromUser);  // send our message to the server
                }

            }

        } catch (UnknownHostException e) {
            System.err.println("Unable to find host " + HOSTNAME + " " + PORTNUMBER);
            System.exit(1);
        }
*/
    }

}
