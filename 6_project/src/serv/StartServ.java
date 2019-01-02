package serv;

import common.PeerTable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartServ {

    private static final Logger LOGGER = Logger.getLogger( ServerSocket.class.getName() );
    PeerTable peerTable = new PeerTable();
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        LOGGER.info("Starting startup server");
        StartServ server = new StartServ();
        int PORT = 48921;
        server.start( PORT );
    }

    private void start ( int PORT ) {
        try {
            LOGGER.info("Waiting for connection from peer");
            serverSocket = new ServerSocket( PORT );
            while (true) {
                new PeerHandler( serverSocket.accept(), this ).run();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    public void stop() {
        try {
            LOGGER.info( "Stopping server" );
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

}
