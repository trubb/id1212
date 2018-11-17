package client;

import client.view.Interpreter;

public class Client {

    private static String HOSTNAME = "127.0.0.1";
    private static final int PORTNUMBER = 48922;

    /**
     * Client main method, run to processConnections a new client.
     * An interpreter is spawned, from which the other components get started
     * @param args No arguments used
     */
    public static void main(String[] args) {
        new Interpreter( HOSTNAME, PORTNUMBER ).start();
    }
}

/**
 * TODO - commands do not appear on client processConnections
 * TODO - game does nothing with input - PROBABLY NEED TO REDO SERVSIDE
 * TODO - enable guessing whole words
 * TODO - COMMENTS
 * TODO - Possibly do-over from client connects to server and can talk b/forth
*/
