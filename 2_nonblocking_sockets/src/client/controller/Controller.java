package client.controller;

import client.net.CommunicationListener;
import client.net.ServerConnection;

import java.io.IOException;
import java.io.UncheckedIOException;

public class Controller {
    ServerConnection serverConnection = new ServerConnection();

    public void connect ( String HOSTNAME, int PORTNUMBER ) {
        try {
            serverConnection.connect( HOSTNAME, PORTNUMBER );
            System.out.println("Connected.\nAllowed commands are PLAY and QUIT\n");
        } catch (IOException e) {
            System.err.println("OOPS");
        }
    }

    public void disconnect() {
        try {
            System.out.println("closing");
            serverConnection.disconnect();
        } catch (IOException e) {
            System.err.println("couldnt disconnect");
            System.exit(1);
        }
    }

    public void sendMessage ( String message ) {
        try {
            serverConnection.sendMessage( message );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void setCommunicationListener ( CommunicationListener communicationListener ) {
        serverConnection.setCommunicationListener(communicationListener);
    }
}
