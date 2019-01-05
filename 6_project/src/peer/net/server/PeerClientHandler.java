package peer.net.server;

import common.MessageType;
import common.Message;
import trackerserver.PeerHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles messages sent from peers
 */
public class PeerClientHandler {

    private static final Logger LOGGER = Logger.getLogger( PeerHandler.class.getName() );
    private final ControllerObserver controllerObserver;
    private Socket clientSocket;

    /**
     * Constructor
     * @param clientSocket          the socket that we want to communicate with
     * @param controllerObserver    the ControllerObserver we will use
     */
    public PeerClientHandler ( Socket clientSocket, ControllerObserver controllerObserver ) {
        this.clientSocket = clientSocket;
        this.controllerObserver = controllerObserver;
    }

    /**
     * Deal with messages received from the peer
     */
    void run() {
        try {
            // create output/inputstreams
            ObjectOutputStream out = new ObjectOutputStream( clientSocket.getOutputStream() );
            ObjectInputStream in = new ObjectInputStream( clientSocket.getInputStream() );

            Message message = (Message) in.readObject();  // read a message from the inputstream

            // deal with the message based on its type
            switch ( message.getMessageType() ) {
                case JOIN:
                    // if it's a join message, add the peer to the list of peers
                    controllerObserver.addPeer( message.getSenderPeerInfo() );
                    // reply with our own info in a sync message
                    out.writeObject( new Message( MessageType.SYNC, controllerObserver.getPeerInfo()) );
                    break;
                case LEAVE:
                    // if it's a leave message, remove the peer from our PeerList
                    controllerObserver.removePeer( message.getSenderPeerInfo() );
                    break;
                case MOVE:
                    // if it's a move message, set the move of this peer for the current round
                    controllerObserver.setPeerMove( message.getMove(), message.getSenderPeerInfo() );
                    break;
                default:
                    // if the message is unrecognizable, do nothing but log it
                    LOGGER.log(Level.SEVERE, "Unknown command");
            }

        } catch (IOException | ClassNotFoundException e) {

        }
    }
}
