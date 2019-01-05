package peer.net.client;

import common.Message;
import common.MessageType;
import peer.net.server.PeerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * For connecting to a peer that is present in the network
 */
public class PeerConnection {

    private Socket peerSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Open connection with a specific peer present in the net
     * @param IP    the peer's IP
     * @param PORT  the peer's PORT
     * @throws IOException
     */
    public void startConnection ( String IP, int PORT ) throws IOException {
        peerSocket = new Socket( IP, PORT );    // create a socket for connecting to the peer
        out = new ObjectOutputStream( peerSocket.getOutputStream() );   // outputstream to the peer
        in = new ObjectInputStream( peerSocket.getInputStream() );      // inputstream from the peer
    }

    /**
     * Send join msg to peer so that they add us to their PeerList
     * @param currentPeerInfo   our PeerInfo
     * @return                  info from the peer we joined
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PeerInfo sendJoinMessage ( PeerInfo currentPeerInfo ) throws IOException, ClassNotFoundException {
        out.writeObject( new Message( MessageType.JOIN, currentPeerInfo ) ); // write a JOIN message to the peer
        return (PeerInfo) in.readObject();  // read its response
    }

    /**
     * Send leave msg to peer so they remove us from their PeerList
     * @param currentPeerInfo   our PeerInfo
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendLeaveMessage ( PeerInfo currentPeerInfo ) throws IOException, ClassNotFoundException {
        out.writeObject( new Message( MessageType.LEAVE, currentPeerInfo ) );    // write a LEAVE message to the peer
    }

    /**
     * Let a peer know that we have selected a move
     * @param move              type of move [rock/paper/scissors]
     * @param currentPeerInfo   our PeerInfo
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendMoveMessage ( String move, PeerInfo currentPeerInfo ) throws IOException, ClassNotFoundException {
        out.writeObject( new Message( MessageType.MOVE, currentPeerInfo, move ) );   // write a MOVE message to the peer
    }

    /**
     * Close the connection with the peer
     * @throws IOException
     */
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        peerSocket.close();
    }
}
