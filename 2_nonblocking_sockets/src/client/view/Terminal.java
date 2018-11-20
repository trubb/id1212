package client.view;

import client.controller.Controller;
import client.net.CommunicationListener;
import shared.MessageFormatter;

import java.io.IOException;
import java.util.Scanner;

public class Terminal implements Runnable {

    private final ThreadSafeStdOut outManager = new ThreadSafeStdOut(); // where we want to print things in a synchronized manner
    private final Scanner console = new Scanner(System.in); // user input source
    private ConsoleOutput consoleOutput = new ConsoleOutput();  // handler for putting things in the right place
    private static final String PROMPT = ">";   // our "hey user you can write here" character
    private boolean running = false;
    private Controller controller;

    /**
     * Custom printing subclass
     */
    private class ConsoleOutput implements CommunicationListener {
        @Override
        public void print(String message) { // we want to print things in a very peculiar way
            outManager.println(message);    // print the message
            outManager.print(PROMPT);   // then print the "hey there you can write here" char
        }
    }

    /**
     * Starts the Terminal class
     */
    public void start() {
        if (running) {  // if we're already running we're not gonna run²
            return;
        }
        running = true; // Now we're running
        controller = new Controller();  // initialize the controller
        controller.setViewListener(consoleOutput);  // pass the output instance to the controller
        new Thread(this).start();   // start the terminal in a new thread
    }

    /**
     * Things we do continuously while running
     */
    @Override
    public void run() {
        controller.connect();   // tell the controller to connect

        while (running) {   // as long as we're running
            try {
                InputReader readLine = new InputReader( console.nextLine() );   // read from the terminal input
                switch (readLine.getCommands()) {   // check what command the user input matches
                    case QUIT:  // if it is QUIT then we disconnect from the server and shut down
                        controller.disconnect();
                        running = false;
                        break;
                    case START: // if it is START then we want to start another round so we do that
                        controller.startNewRound();
                        break;
                    case GUESS: // if it is a GUESS then we pass the input to the controller
                        controller.submitGuess( readLine.getArgument(0) );
                        break;
                    case NO_COMMAND:    // if no input given then print the prompt character again
                        outManager.print(PROMPT);
                        break;
                }
            } catch (IOException e) {   // if something stupid happens then print the error and a new prompt char
                outManager.print( MessageFormatter.inputError( e.getMessage() ) );
                outManager.print(PROMPT);
            }
        }
    }

}
