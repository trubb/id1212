package client.view;

import client.controller.Controller;
import client.net.CommunicationListener;
import shared.PrettyPrinter;

import java.io.IOException;
import java.util.Scanner;

public class Terminal implements Runnable {

    private static final String PROMPT = ">";
    private final ThreadSafeStdOut outManager = new ThreadSafeStdOut();
    private final Scanner console = new Scanner(System.in);
    private ConsoleOutput consoleOutput = new ConsoleOutput();
    private boolean running = false;
    private Controller controller;

    private class ConsoleOutput implements CommunicationListener {
        @Override
        public void print(String msg) {
            outManager.println(msg);
            outManager.print(PROMPT);
        }
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        controller = new Controller();
        controller.setViewListener(consoleOutput);
        new Thread(this).start();
    }

    @Override
    public void run() {
        outManager.println( PrettyPrinter.buildWelcomeMessage() );
        outManager.println( PrettyPrinter.buildStartInfoMessage() );
        outManager.print(PROMPT);

        while (running) {
            try {
                InputReader readLine = new InputReader( console.nextLine() );
                switch (readLine.getCommands()) {
                    case CONNECT:
                        controller.connect();
                        break;
                    case QUIT:
                        controller.disconnect();
                        running = false;
                        break;
                    case START:
                        controller.startNewRound();
                        break;
                    case GUESS:
                        controller.submitGuess( readLine.getArgument(0) );
                        break;
                    case NO_COMMAND:
                        outManager.print(PROMPT);
                        break;
                }
            } catch (IOException e) {
                outManager.print( PrettyPrinter.buildCommandErrorMessage( e.getMessage() ) );
                outManager.print(PROMPT);
            }
        }
    }

}
