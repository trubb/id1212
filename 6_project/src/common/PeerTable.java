package common;

import peer.net.server.PeerInfo;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class PeerTable implements Serializable {
    private HashMap<String, PeerInfo> peerTable;

    public PeerTable() {
        this.peerTable = new HashMap<>();
    }

    public void addPeerToTable(PeerInfo peerInfo) {
        this.peerTable.put(peerInfo.getID(), peerInfo);
    }

    public void removePeerFromTable(String id) {
        this.peerTable.remove(id);
    }

    public Collection<PeerInfo> getPeerInfo() {
        return peerTable.values();
    }

    public HashMap<String, PeerInfo> getPeerTable() {
        return peerTable;
    }

    public int getTableSize() {
        return peerTable.size();
    }

    public void replacePeer(PeerInfo peerInfo) {
        this.peerTable.replace(peerInfo.getID(), peerInfo);
    }

    public void resetPeersMoves() {
        for ( PeerInfo peer : this.peerTable.values() ) {
            peer.setCurrentMove( null );
        }
    }

    public boolean allPeersPlayed() {
        boolean allPlayed = true;

        for ( PeerInfo peer : this.peerTable.values() ) {
            if ( peer.getCurrentMove() == null ) {
                allPlayed = false;
            }
        }
        return allPlayed;
    }

    public void setPeerMove ( PeerInfo peerInfo, String move ) {
        peerTable.get( peerInfo.getID() ).setCurrentMove( move );
    }

    @Override
    public String toString() {
        return "Peertable: {" + peerTable + "}";
    }
}
