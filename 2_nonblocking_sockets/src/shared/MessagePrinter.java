package shared;

/**
 * Nicely formatted messages are found here
 * Use for displaying information sent from the server in a better way than in homework 1
 */
public class MessagePrinter {

    /**
     * @param reason why did this happen?
     * @return formatted message
     */
    public static String inputError(String reason) {
        return "Command error: " + reason + "\n";
    }

    /**
     * Used for treating the guess replies from the server nicely
     * @param message the game state
     * @return formatted message
     */
    public static String guessReply(String message) {
        return message;
    }

    /**
     * Used for providing an end-of-round message
     * @param message game state at end of round
     * @return formatted message + helper text
     */
    public static String endReply(String message) {
        return "Round over, result:\n" + message + "\n" +
                "start a new round with \"start\" or quit with \"quit\"\n" +
                "No other commands are permitted.";
    }

    /**
     * Used at the start of a round
     * @param message game state for freshly initialized round
     * @return formatted message
     */
    public static String startGuess(String message) {
        return "new game started\n" + message;
    }

    /**
     * Used at startup to provide information to the user when the client has connected to the server
     * @return helper text
     */
    public static String clientStartInfo() {
        return "connected\n" + "start game with \"start\", quit with \"quit\"";
    }
}
