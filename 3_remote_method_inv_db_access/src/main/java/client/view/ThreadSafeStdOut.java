package client.view;

class ThreadSafeStdOut {
    /**
     * A thread safe way to print things
     * @param output the input but synchronized
     */
    synchronized void print (String output) {
        System.out.print(output);
    }

    /**
     * A thread safe way to print things on its own line
     * @param output the input but synchronized and on a separate line
     */
    synchronized void println (String output) {
        System.out.println(output);
    }
}