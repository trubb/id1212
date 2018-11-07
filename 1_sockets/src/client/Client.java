package client;

import client.view.View;

import java.io.IOException;

public class Client {

    private static String HOSTNAME = "130.229.159.12";
    private static final int PORTNUMBER = 48921;

    public static void main(String[] args) throws IOException {

        Controller controller = new Controller();
        controller.connect( HOSTNAME, PORTNUMBER );

        View view = new View( controller );
        view.start();

    }

}
