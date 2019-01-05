package peer.view;

import common.PreparedMessages;
import peer.controller.Controller;
import peer.net.server.OutputHandler;

import java.util.Scanner;

/**
 * Responsible for dealing with user input and displaying results to the user
 */
public class GameShell implements Runnable {

    private static final String PROMPT = ">";
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut(); // A nice way to print things
    private final Scanner console = new Scanner( System.in );       // for dealing with user input
    private boolean running = false;                                // we haven't started yet
    private Controller controller;                                  // the controller that we shall be using

    /**
     * Start the GameShell
     */
    public void start() {
        if ( running ) {    // if already running just exit the method
            return;
        }
        running = true;
        controller = new Controller( new ConsoleOutput() ); // initialize a new Controller
        new Thread(this).start();   // get things going
    }

    /**
     * Continuous running of the shell
     */
    @Override
    public void run() {
        outMgr.print( PreparedMessages.startMessage() );    // print the informative start message
        outMgr.print( PROMPT );                             // print the prompt char

        while (running) {
            try {
                // create a LineParser and have it take a look at the line input by the user
                LineParser parsedLine = new LineParser( console.nextLine() );
                // based on what was provided we can do a number of things
                switch ( parsedLine.getCommand() ) {
                    case CONNECT:   // if we are to connect to a tracker
                        controller.joinNetwork( // tell the controller to connect to the specified server
                                parsedLine.getArgument( 0 ),                        // IP
                                Integer.parseInt( parsedLine.getArgument( 1 ) ) );  // PORT
                        break;
                    case QUIT:  // if we are to disconnect from the network
                        controller.leaveNetwork();  // leave the network
                        running = false;    // stop the gameshell
                        break;
                    case MAKE_MOVE: // if we are to make a move, send the move through the controller
                        controller.sendMove( parsedLine.getArgument( 0 ), new ConsoleOutput() );
                        break;
                    case NO_COMMAND:    // if what is provided isn't recognizable as a command ignore it
                        outMgr.print( PROMPT );
                        break;
                }
            } catch (IllegalArgumentException e) {
                outMgr.print( PreparedMessages.commandErrorMessage( e.getMessage() ) );
                outMgr.print( PROMPT );
            }
        }
    }

    /**
     * Implementation of the OutputHandler
     * Used to print things
     */
    private class ConsoleOutput implements OutputHandler {
        /**
         * Print the provided string to the client's terminal
         * @param msg   the message received
         */
        @Override
        public void handleMsg ( String msg ) {
            outMgr.println( msg );
            outMgr.print( PROMPT );
        }
    }
}
