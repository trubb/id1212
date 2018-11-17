package client.view;

import client.ConsoleOutput;
import client.controller.Controller;
import client.net.CommunicationListener;

import java.util.Scanner;

/**
 * Interprets user input and performs actions based on it
 */
public class Interpreter implements Runnable {

    private String HOSTNAME;
    private int PORTNUMBER;

    private Controller controller;
    private final Scanner consoleInput = new Scanner(System.in);
    private final ThreadSafeStdOut threadSafeStdOut = new ThreadSafeStdOut();

    private ConsoleOutput consoleOutput = new ConsoleOutput();
    private boolean receivingCmds = false;
    private boolean running = false;

    /**
     * Target correct server
     * @param HOSTNAME ip address of target server
     * @param PORTNUMBER port number to use
     */
    public Interpreter ( String HOSTNAME, int PORTNUMBER ) {
        this.HOSTNAME = HOSTNAME;
        this.PORTNUMBER = PORTNUMBER;
    }

    /**
     * Setup controller and processConnections run method
     */
    public void start() {
        if (running) return;

        running = true;

        controller = new Controller();
        controller.setCommunicationListener( consoleOutput );

        new Thread(this).start(); // processConnections thread to run continuously
    }

    /**
     *
     */
    @Override
    public void run() {
        threadSafeStdOut.print("Client started\nAllowed commands are PLAY and QUIT");

        while (running) {
            try {
                CommandLine commandLine = new CommandLine( consoleInput.nextLine() );

                if ( commandLine.getCommand() == Command.QUIT ) {
                    controller.disconnect();
                    running = false;
                } else if ( commandLine.getCommand() == Command.PLAY ) {
                    controller.connect( HOSTNAME, PORTNUMBER );
                } else if ( commandLine.getCommand() == Command.SEND ) {
                    controller.sendMessage( commandLine.getArgument(0) );
                } else {
                    threadSafeStdOut.print(">");
                }

            } catch (Exception e) {
                threadSafeStdOut.println("Interpreter made an oopsie");
            }
        }
    }

    private class ConsoleOutput implements CommunicationListener {
        @Override
        public void print ( String message ) {
            threadSafeStdOut.println( message );
            threadSafeStdOut.print(">");
        }
    }

}
