package peer.net.server;

public interface OutputHandler {

    /**
     * Called when a message has been received
     * @param msg   the message received
     */
    public void handleMsg ( String msg );
}
