package client.controller;

import client.net.CommunicationListener;
import client.net.ServerConnection;

import java.io.IOException;

public class Controller {

    // Spawn a new serverConnection so we can connect to the server
    ServerConnection serverConnection = new ServerConnection();

    /**
     * Connect to the server on demand
     */
    public void connect () {
        serverConnection.connect();
    }

    /**
     * Disconnect from the server
     * @throws IOException
     */
    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    /**
     * Start a new game round
     */
    public void startNewRound() {
        serverConnection.startNewRound();
    }

    /**
     * Post a guess to the server
     * @param guess the client's guess
     */
    public void submitGuess (String guess) {
        serverConnection.submitGuess(guess);
    }

    /**
     * Pass the listener to the serverconnection
     * @param listener
     */
    public void setViewListener (CommunicationListener listener) {
        serverConnection.setViewListener(listener);
    }

}
