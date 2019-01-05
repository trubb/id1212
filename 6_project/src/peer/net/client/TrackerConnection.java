package peer.net.client;

import common.Message;
import common.MessageType;
import common.PeerList;
import peer.net.server.PeerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * For connecting to the tracker server
 */
public class TrackerConnection {

    private Socket peerSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Opens a connection with the start server - the "tracker"
     * @param IP    IP we want to connect to
     * @param PORT  PORT we want to connect to
     * @throws IOException
     */
    public void startConnection ( String IP, int PORT ) throws IOException {
        peerSocket = new Socket( IP, PORT );
        out = new ObjectOutputStream( peerSocket.getOutputStream() );
        in = new ObjectInputStream( peerSocket.getInputStream() );
    }

    /**
     * Sends a join message to the server to add uss to the list of peers
     * Server replies with a full list of clients currently in the network
     * @param currentPeerInfo   the PeerInfo of this client
     * @return                  the list of peers that the server holds
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PeerList sendJoinMessage (PeerInfo currentPeerInfo ) throws IOException, ClassNotFoundException {
        out.writeObject( new Message( MessageType.JOIN, currentPeerInfo ) ); // write a messageWrapper to the out stream
        PeerList peerList = (PeerList) in.readObject(); // cast the read object to a PeerList
        peerList.removePeerFromList( currentPeerInfo.getID() ); // remove "US" from the the list, we arent a peer of ourself
        return peerList;
    }

    /**
     * Sends a leave message to the server so that we are removed from the list of clients in the net
     * @param currentPeerInfo   the PeerInfo of this client
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendLeaveMessage ( PeerInfo currentPeerInfo ) throws IOException, ClassNotFoundException {
        out.writeObject( new Message( MessageType.LEAVE, currentPeerInfo ) );
    }

    /**
     * Close streams and socket
     * @throws IOException
     */
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        peerSocket.close();
    }
}
