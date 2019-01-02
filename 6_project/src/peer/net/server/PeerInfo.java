package peer.net.server;

public class PeerInfo {

    private String ID;
    private String HOST;
    private int PORT;

    private String currentMove;
    private int roundScore;
    private int totalScore;

    public PeerInfo(String ID, String HOST, int PORT) {
        this.ID = ID;
        this.HOST = HOST;
        this.PORT = PORT;
        this.currentMove = null;
        this.roundScore = 0;
        this.totalScore = 0;
    }

    /**
     * TODO - CHECK WHAT GOES ON HERE
     * @param HOST
     * @param PORT
     */
    public PeerInfo(String HOST, int PORT) {
        this( HOST + ":" + PORT, HOST, PORT );
    }

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

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
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

    public void resetTotalScore() {
        this.totalScore = 0;
    }

    @Override
    public String toString() {
        return ID;
    }
}
