package client.view;

public class ThreadSafeStdOut {

    /**
     * A synchronized way to print things
     * @param output the input but synchronized
     */
    public synchronized void print ( String output ) {
        System.out.print( output );
    }

    /**
     * A synchronized way to print things on its own line
     * @param output the input but synchronized and on a separate line
     */
    public synchronized void println ( String output ) {
        System.out.println( output );
    }
}
