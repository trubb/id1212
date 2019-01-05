package peer;

import peer.view.GameShell;

/**
 * The peer to peer client main method
 */
public class PeerToPeerClient {

    /**
     * Start the client
     * @param args
     */
    public static void main(String[] args) {
        new GameShell().start();
    }
}
