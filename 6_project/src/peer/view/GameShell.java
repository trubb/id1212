package peer.view;

import peer.controller.Controller;
import peer.net.server.OutputHandler;

import java.util.Scanner;

public class GameShell implements Runnable {

    private static final String PROMPT = ">";
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private final Scanner console = new Scanner( System.in );
    private boolean running = false;
    private Controller controller;

    public void start() {
        if ( running ) {
            return;
        }
        running = true;

        controller = new Controller( new ConsoleOutput() );
    }


    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {
            outMgr.println( msg );
            outMgr.print( PROMPT );
        }
    }
}
