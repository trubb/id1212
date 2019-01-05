package peer.net.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Deals with connections from other peers
 */
public class PeerServer implements Runnable {

    private ServerSocket serverSocket;
    private ControllerObserver controllerObserver;

    /**
     * Start the PeerServer
     * @param serverSocket          the serversocket that we will be using
     * @param controllerObserver    the controllerobserver that we will be using
     */
    public void start ( ServerSocket serverSocket, ControllerObserver controllerObserver ) {
        this.serverSocket = serverSocket;
        this.controllerObserver = controllerObserver;
        new Thread( this ).start(); // start the PeerServer instance
    }

    /**
     * Run the PeerServer
     */
    @Override
    public void run() {
        try {
            while (true) {
                // start a PeerClientHandler and have it deal with the connecting users
                new PeerClientHandler( serverSocket.accept(), controllerObserver ).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the PeerServer
     */
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
