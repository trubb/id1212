package server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnector extends Thread {

    private Socket socket = null;

    public ClientConnector ( Socket socket ) {
        super("ClientConnector");
        this.socket = socket;
    }

    public void run() {
        try (
            PrintWriter messageToClient = new PrintWriter( socket.getOutputStream(), true );
            BufferedReader input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        ) {
            String instring, outstring;
            outstring = "Hello, you connected to the server";
            messageToClient.println( outstring );

            while ( ( instring = input.readLine() ) != null ) {
                outstring = "The server received your message which was: " + instring;
                messageToClient.println( outstring );

                if ( instring.equals("!QUIT") ) {
                    outstring = "BYE!";
                    messageToClient.println( outstring );
                    break;
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
