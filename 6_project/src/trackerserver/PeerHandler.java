package trackerserver;

import common.MessageType;
import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the list of clients that are connected to the network
 */
public class PeerHandler {

    private static final Logger LOGGER = Logger.getLogger( PeerHandler.class.getName() );
    private Socket clientSocket;
    private TrackerServer server;

    /**
     * Constructor
     * @param clientSocket  the socket that the client is connected to
     * @param server        the server this PeerHandler belongs to
     */
    public PeerHandler(Socket clientSocket, TrackerServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void run() {
        try {
            // initialize output/inputstreams
            ObjectOutputStream out = new ObjectOutputStream( clientSocket.getOutputStream() );
            ObjectInputStream in = new ObjectInputStream( clientSocket.getInputStream() );

            Message message = (Message) in.readObject();    // read message sent by the connected client

            // if the message type is JOIN we add them to the server's PeerList
            if ( message.getMessageType().equals( MessageType.JOIN ) ) {
                LOGGER.info( "Peer at "+ message.getSenderPeerInfo().getPORT() + " joined network" );
                server.peerList.addPeerToList( message.getSenderPeerInfo() );
                out .writeObject( server.peerList );
                System.out.println( server.peerList );
                // if the message type is LEAVE we remove them from the server's PeerList
            } else if ( message.getMessageType().equals( MessageType.LEAVE ) ) {
                LOGGER.info( "Peer at " + message.getSenderPeerInfo().getPORT() + " left network" );
                server.peerList.removePeerFromList( message.getSenderPeerInfo().getID() );
                System.out.println( server.peerList );
            }

            in.close();
            out.close();
            clientSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
}
