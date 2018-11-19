package shared;

public class PrettyPrinter {

    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char VERTICAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "─────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    public static String buildWelcomeMessage() {
        return "\033[36m" + TOP_BORDER + "\n" + VERTICAL_LINE + "  WELCOME TO NIO HANGMAN \n" + BOTTOM_BORDER + "\033[0m" + "\n";
    }

    public static String buildCommandErrorMessage(String reason) {
        return "\033[31m" + "Command error: " + reason + "\033[0m" + "\n";
    }

    public static String buildNetworkErrorMessage(String reason) {
        return "\033[31m" + "Network error: " + reason + "\033[0m" + "\n";
    }

    public static String buildGuessResponseMessage(String message) {
        return "\033[36m" + message + "\033[0m" + "\n";
    }

    public static String buildEndResponseMessage(String message) {
        return "\033[36m" + TOP_BORDER + "\n" + VERTICAL_LINE + " The game is ended! Here is the result:" +
                "\n" + VERTICAL_LINE + " " + message + "\n" + BOTTOM_BORDER + "\n" + "\033[33m" +
                "You can start a new game with '\033[35mstart\033[33m'" + "\033[0m" + "\n";
    }

    public static String buildMakeGuessMessage(String message) {
        return "\033[36m" + TOP_BORDER + "\n" + VERTICAL_LINE + " You have started a new game!" + "\n" + MIDDLE_BORDER +
                "\n" + VERTICAL_LINE + " " + message + "\n" + BOTTOM_BORDER + "\n" + "\033[33m" +
                "Go on and type a guess for a letter or the word; you can always quit with '\033[35mquit\033[33m'." +
                "\033[0m" + "\n";
    }

    public static String buildStartInfoMessage() {
        return "\033[33m" + "Connect to a server with '\033[35mconnect <ip> <port>\033[33m'\n" +
                "Quit the game with '\033[35mquit\033[33m'" + "\033[0m" + "\n";
    }

    public static String buildStartGameMessage() {
        return "\033[33m" + "Connection successful!\n" +
                "You can start a new game with '\033[35mstart\033[33m'" + "\033[0m" + "\n";
    }
}
