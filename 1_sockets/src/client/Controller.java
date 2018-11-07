package client;

import client.net.ServerConnector;

import java.io.IOException;

public class Controller {

    private ServerConnector connection;

    public void connect ( String HOSTNAME, int PORTNUMBER ) throws IOException {

        try {
            connection = new ServerConnector();
            connection.connect( HOSTNAME, PORTNUMBER );
            System.out.println("Controller: Connected to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect () throws IOException {
        try {
            System.out.println("Controller: Connection closing!");
            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Controller: Could not disconnect!");
            System.exit(1);
        }
    }

    public void sendMessage ( String message ) throws IOException {
        try {
            connection.sendMessage( message );
        } catch (IOException e) {
            System.out.println("Controller: Failed to send message!");
        }

    }

}
