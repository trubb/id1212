package shared;

public class MessagePrinter {

    public static String inputError(String reason) {
        return "\033[31m" + "Command error: " + reason + "\033[0m" + "\n";
    }

    public static String networkError(String reason) {
        return "\033[31m" + "Network error: " + reason + "\033[0m" + "\n";
    }

    public static String guessReply(String message) {
        return "\033[36m" + message + "\033[0m" + "\n";
    }

    public static String endReply(String message) {
        return "Game over, result: " + message + "\n" + "start a new game with \"start\"\n";
    }

    public static String startGuess(String message) {
        return "new game started " + message + "you can now guess, quit with \"quit\"\n";
    }

    public static String startInfo() {
        return "connect to the server: \"connect\"\n" + "exit the client with \"quit\"\n";
    }

    public static String gameStartInfo() {
        return "connected\n" + "start game with \"start\"\n";
    }
}
