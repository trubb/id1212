package client.view;

public class ThreadSafeStdOut {
    public synchronized void print ( String output ) {
        System.out.print( output );
    }

    public synchronized void println ( String output ) {
        System.out.println( output );
    }
}
