package client;

import client.view.ClientInput;
import client.controller.Controller;

import java.io.IOException;

public class Client {

    private static String HOSTNAME = "127.0.0.1";
    private static final int PORTNUMBER = 48921;

    /**
     * Client main method, run to start a new client.
     * A new controller is spawned, which in turn starts a connection to the server
     * @param args No arguments used
     * @throws IOException Occurs if something inadvertedly goes wrong
     */
    public static void main(String[] args) throws IOException {

        Controller controller = new Controller();
        controller.connect( HOSTNAME, PORTNUMBER );

        ClientInput clientInput = new ClientInput( controller );
        clientInput.start();
    }

}
