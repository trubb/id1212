package client.view;

public enum Commands {
    START,  // Start a new game round
    GUESS,  // Send a letter or word guess
    QUIT,   // Quit the game and disconnect
    NO_COMMAND  // Neither of these operations
}