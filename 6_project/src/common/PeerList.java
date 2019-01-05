package common;

import peer.net.server.PeerInfo;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * Holder for the list of peers that are part of the network
 */
public class PeerList implements Serializable {

    private HashMap<String, PeerInfo> peerList;

    /**
     * Constructor
     */
    public PeerList() {
        this.peerList = new HashMap<>();
    }

    /**
     * Adds a new client (peer) to the list of peers
     * @param peerInfo  the client's information
     */
    public void addPeerToList(PeerInfo peerInfo ) {
        this.peerList.put( peerInfo.getID(), peerInfo );
    }

    /**
     * Removes a client (peer) from the list of peers
     * @param id    the id identifying the peer that is to be removed
     */
    public void removePeerFromList(String id ) {
        this.peerList.remove( id );
    }

    /**
     * Getter for the peers in the list
     * @return  the peers in the list as a Collection
     */
    public Collection<PeerInfo> getPeerInfo() {
        return peerList.values();
    }

    /**
     * Getter for the full list of peers
     * @return  the list of peers
     */
    public HashMap<String, PeerInfo> getPeerList() {
        return peerList;
    }

    /**
     * Getter for the size of the list (number of peers)
     * @return  the number of clients in the list
     */
    public int getTableSize() {
        return peerList.size();
    }

    /**
     * replace a peer in the list
     * @param peerInfo
     */
    public void replacePeer ( PeerInfo peerInfo ) {
        this.peerList.replace( peerInfo.getID(), peerInfo );
    }

    /**
     * Set the move of a specific peer
     * @param peerInfo  the peer we will set the move for
     * @param move      the move the peer has selected
     */
    public void setPeerMove ( PeerInfo peerInfo, String move ) {
        peerList.get( peerInfo.getID() ).setCurrentMove( move );
    }

    /**
     * Reset the peer's moves
     */
    public void resetPeerMoves() {
        for ( PeerInfo peer : this.peerList.values() ) {   // for each peer in the peerList
            peer.setCurrentMove( null );    // set the current move to be null
        }
    }

    /**
     * Check if all peers in the list has submitted a move
     * @return  true if all have played, false if not
     */
    public boolean allPeersPlayed() {
        boolean allPlayed = true;

        for ( PeerInfo peer : this.peerList.values() ) {
            if ( peer.getCurrentMove() == null ) {
                allPlayed = false;
            }
        }
        return allPlayed;
    }

    /**
     * Serializer for the peer list
     * @return  the peer list as a string
     */
    @Override
    public String toString() {
        return "Peertable: {" + peerList + "}";
    }
}
