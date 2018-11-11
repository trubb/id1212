package client.view;

import client.Controller;

import java.io.IOException;
import java.util.Scanner;

public class View implements Runnable {

    private Controller controller;
    private boolean receivingCommands = false;
    private Scanner stdIn = new Scanner(System.in);

    /**
     * Sets the provided controller as the one to work with
     * @param controller the controller that is to be used
     */
    public View ( Controller controller ) {
        this.controller = controller;
    }

    /**
     * Starts this instance of the view
     */
    public void start () {
        if (receivingCommands) {
            return;
        }
        receivingCommands = true;
        new Thread(this).start();
    }

    /**
     * @return input to be passed to another function
     */
    private String nextLine() {
        return stdIn.nextLine();
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
                } else if ( input.equals("!PLAY") ) {
                    controller.sendMessage( input );
                } else {
                    controller.sendMessage( input );
                }

            } catch ( IOException e ) {
                System.err.println("Message input fail in view");
            }
        }
    }


}
