package common;

/**
 * Contains predefined messages to ensure that we do not rewrite similar messages in different places
 */
public class PreparedMessages {

    /**
     * The message displayed when a client is started
     * @return
     */
    public static String startMessage() {
        return "welcome 2 p2p rock paper scissors\n" +
                "connect to a p2p net with connect <ip> <port>\n" +
                "available moves are [rock/paper/scissors]\n" +
                "quit with quit\n";
    }

    /**
     * The message displayed when an unknown command is provided by the user
     * @param reason
     * @return
     */
    public static String commandErrorMessage ( String reason ) {
        return "command error: " + reason + "\n";
    }

    /**
     * The message displayed when there was a problem with the connection
     * @param reason
     * @return
     */
    public static String networkErrorMessage ( String reason ) {
        return "network error: " + reason + "\n";
    }

    /**
     * The message displayed at the end of a round
     * @param message
     * @return
     */
    public static String scoreMessage ( String message ) {
        return message + "\nA new round started, you can quit by writing quit\n";
    }

    /**
     * The message displayed when a player is alone in a network and has made a move
     * Indicating that their move has been stored but is not yet relevant
     * @return
     */
    public static String waitForPeersMessage() {
        return "no other players in the net\n" +
                "you have made your move, wait for other players to join\n";
    }

    /**
     * The message displayed when a client has connected to the net
     * @param peerNumber
     * @return
     */
    public static String onConnectMessage ( int peerNumber ) {
        return "connected\nthere are: " + peerNumber + " players in this net\n" +
                "submit your move of [rock/paper/scissors] and wait for the rest to make their moves";
    }
}
