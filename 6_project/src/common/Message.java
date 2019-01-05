package common;

import peer.net.server.PeerInfo;

import java.io.Serializable;

/**
 * A wrapper for sending information about an action:
 * What action it is
 * Who sent it
 * if applicable what move was made [rock/paper/scissors]
 */
public class Message implements Serializable {

    private MessageType messageType;
    private PeerInfo senderPeerInfo;
    private String move;

    /**
     * If there is no move to be sent because we are joining or leaving a net
     * @param messageType
     * @param senderPeerInfo
     */
    public Message ( MessageType messageType, PeerInfo senderPeerInfo ) {
        this.messageType = messageType;
        this.senderPeerInfo = senderPeerInfo;
        this.move = null;
    }

    /**
     * If there IS a move to send then we include it, eg when making a move
     * @param messageType
     * @param senderPeerInfo
     * @param move
     */
    public Message ( MessageType messageType, PeerInfo senderPeerInfo, String move ) {
        this.messageType = messageType;
        this.senderPeerInfo = senderPeerInfo;
        this.move = move;
    }

    /**
     * Getter for message type
     * @return
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Getter for peer info
     * @return
     */
    public PeerInfo getSenderPeerInfo() {
        return senderPeerInfo;
    }

    /**
     * Getter for move
     * @return
     */
    public String getMove() {
        return move;
    }

    /**
     * Returns the content of the wrapper as a string
     * @return  all info as a nice string
     */
    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", senderPeerInfo=" + senderPeerInfo +
                ", move='" + move + '\'' + '}';
    }
}
