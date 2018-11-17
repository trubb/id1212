package client;

import client.view.ThreadSafeStdOut;

public class ConsoleOutput {
    private String PROMPT = ">";
    private final ThreadSafeStdOut threadSafeStdOut = new ThreadSafeStdOut();

    public void handleMsg( String message ) {
        threadSafeStdOut.println( (String) message );
        threadSafeStdOut.print( PROMPT );
    }
}
