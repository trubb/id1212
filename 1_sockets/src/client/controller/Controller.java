package client.controller;

import client.net.ServerConnector;

import java.io.IOException;

public class Controller {

    private ServerConnector connection;

    /**
     * Asks the ServerConnector to start a new connection to the specified IP over the specified port
     * @param HOSTNAME the ip address of the server that we will connect to
     * @param PORTNUMBER the port that will be used
     * @throws IOException in case we couldn't connect
     */
    public void connect ( String HOSTNAME, int PORTNUMBER ) throws IOException {
        try {
            connection = new ServerConnector();
            connection.connect( HOSTNAME, PORTNUMBER );
            System.out.println("Connected.\nAllowed commands are !PLAY and !QUIT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the server
     * @throws IOException in case something goes wrong
     */
    public void disconnect () throws IOException {
        try {
            System.out.println("Disconnected.");
            connection.disconnect();
        } catch (IOException e) {
            System.err.println("Could not disconnect!");
            System.exit(1);
        }
    }

    /**
     * Send a message to the server through the serverconnection
     * @param message
     * @throws IOException
     */
    public void sendMessage ( String message ) throws IOException {
        try {
            connection.sendMessage( message );
        } catch (IOException e) {
            System.err.println("Failed to send message!");
        }

    }

}
