package trackerserver;

import common.PeerList;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The "tracker" server, enabling clients to find each other when connecting to a network
 * Doesn't do anything but ensure discoverability of peers by holding a
 * list of clients on the net much like how a bittorrent tracker works
 */
public class TrackerServer {

    private static final Logger LOGGER = Logger.getLogger( ServerSocket.class.getName() );
    PeerList peerList = new PeerList();  // List of connected clients
    private ServerSocket serverSocket;

    /**
     * Initialize the server on the specified port
     * @param args unused
     */
    public static void main(String[] args) {
        LOGGER.info("Starting startup server");
        TrackerServer server = new TrackerServer();
        int PORT = 48921;       // the port we are listening on
        server.start( PORT );   // start the server on this port
    }

    /**
     * Start the server
     * @param PORT  the port we are to use
     */
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

}
