package server.net;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private static final int PORTNUMBER = 48921;

    /**
     * Server main method, run to start the server.
     * A new ClientConnector is spawned for each client that connects
     * @param args No arguments used
     * @throws IOException Occurs if another service is using the port
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Server starting");
        try (
            ServerSocket serverSocket = new ServerSocket( PORTNUMBER );
        ) {
            while (true) {
                new ClientConnector( serverSocket.accept() ).start();
                System.out.println("A client connected");
            }
        } catch (IOException e) {
            System.out.println("Couldnt listen on port: " + PORTNUMBER);
            System.exit(1);
        }

    }

}
