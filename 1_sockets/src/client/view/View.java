package client.view;

import client.Controller;

import java.io.IOException;
import java.util.Scanner;

public class View implements Runnable {

    private Controller controller;
    private boolean receivingCommands = false;
    private Scanner stdIn = new Scanner(System.in);

    public View ( Controller controller ) {
        this.controller = controller;
    }

    public void start () {
        if (receivingCommands) {
            return;
        }
        receivingCommands = true;
        new Thread(this).start();
    }

    private String nextLine() {
        return stdIn.nextLine();
    }

    @Override
    public void run() {
        while (true) {
            try {
                CommandLine commandLine = new CommandLine( nextLine() );
                String input = commandLine.getInput();

                if ( input.equals("!QUIT") ) {
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
