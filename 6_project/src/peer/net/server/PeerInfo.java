package peer.net.server;

import java.io.Serializable;

/**
 * Contains information about a given client - a peer
 */
public class PeerInfo implements Serializable {

    private String ID;              // the id of the client, literally "hostname:port"
    private String HOST;            // the IP address of the client
    private int PORT;               // the port number of the client
    private String currentMove;     // the move that has been selected for the current round
    private int roundScore;         // the score the client got this round
    private int totalScore;         // the client's total score

    /**
     * Constructor
     * @param HOST
     * @param PORT
     */
    public PeerInfo ( String HOST, int PORT ) {
        this.ID = HOST + ":" + PORT;
        this.HOST = HOST;
        this.PORT = PORT;
        this.currentMove = null;
        this.roundScore = 0;
        this.totalScore = 0;
    }

    // Getters and Setters live below here

    public String getID() {
        return ID;
    }

    public String getHOST() {
        return HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public String getCurrentMove() {
        return currentMove;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setCurrentMove(String currentMove) {
        this.currentMove = currentMove;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore += roundScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore += totalScore;
    }

    public void resetRoundScore() {
        this.roundScore = 0;
    }

    /**
     * Override toString() so that we return the ID
     * @return  the ID as a string
     */
    @Override
    public String toString() {
        return ID;
    }
}
