package peer.net.server;

import common.Message;
import common.MessageWrapper;
import serv.PeerHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeerClientHandler {

    private static final Logger LOGGER = Logger.getLogger( PeerHandler.class.getName() );
    private final ControllerObserver controllerObserver;
    private Socket clientSocket;

    public PeerClientHandler(Socket clientSocket, ControllerObserver controllerObserver) {
        this.controllerObserver = controllerObserver;
        this.clientSocket = clientSocket;
    }

    void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream( clientSocket.getOutputStream() );
            ObjectInputStream in = new ObjectInputStream( clientSocket.getInputStream() );

            MessageWrapper message = (MessageWrapper) in.readObject();

            switch ( message.getMessage() ) {
                case JOIN:
                    controllerObserver.addPeer( message.getSenderPeerInfo() );
                    out.writeObject( new MessageWrapper( Message.SYNC, controllerObserver.getPeerInfo()) );
                    break;
                case LEAVE:
                    controllerObserver.removePeer( message.getSenderPeerInfo() );
                    break;
                case MOVE:
                    controllerObserver.setPeerMove( message.getMove(), message.getSenderPeerInfo() );
                    break;
                default:
                    LOGGER.log(Level.SEVERE, "Unknown command");
            }

        } catch (IOException | ClassNotFoundException e) {

        }
    }
}
