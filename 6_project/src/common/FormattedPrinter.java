package common;

public class FormattedPrinter {

    public static String buildWelcomeMessage() {
        return "welcome 2 p2p rock paper scissors\n";
    }

    public static String buildCommandErrorMessage ( String reason ) {
        return "command error: " + reason + "\n";
    }

    public static String buildNetworkErrorMessage ( String reason ) {
        return "network error: " + reason + "\n";
    }

    public static String buildScoreMessage ( String message ) {
        return message + "\n" + "A new game started, you can quit by writing quit\n";
    }

    public static String buildStartInfoMessage() {
        return "connect to a p2p net with connect <ip> <port>\nquit with quit\n";
    }

    public static String buildWaitingPeersMessage() {
        return "you have made your move, wait for other players to join\n";
    }

    public static String buildSuccessfulConnectionMessage (int peerNumber ) {
        return "connection succeeded, there are: " + peerNumber + " players in this net\n" +
                "submit your rock/paper/scissors move and wait for the rest to make their moves\n";
    }
}
