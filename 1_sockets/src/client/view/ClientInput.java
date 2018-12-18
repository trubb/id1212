package client.view;

import client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

/**
 * Handles user input and parses it to
 */
public class ClientInput implements Runnable {

    private Controller controller;
    private boolean receivingCommands = false;
    private Scanner stdIn = new Scanner(System.in);

    /**
     * Sets the provided controller as the one to work with
     * @param controller the controller that is to be used
     */
    public ClientInput( Controller controller ) {
        this.controller = controller;
    }

    /**
     * Starts this instance of the controller
     */
    public void start () {
        if (receivingCommands) {
            return;
        }
        receivingCommands = true;
        new Thread(this).start();
    }

    /**
     * Take in information
     */
    @Override
    public void run() {
        while (true) {
            try {
                String input = stdIn.nextLine();

                if ( input.equals("!QUIT") ) {
                    controller.sendMessage( input );
                    receivingCommands = false;
                    controller.disconnect();
                    break;
                } else {
                    controller.sendMessage( input );
                }

            } catch ( IOException e ) {
                System.err.println("Message input fail in controller");
            }
        }
    }

}
