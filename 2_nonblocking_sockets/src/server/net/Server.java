package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private int PORTNUMBER = 48922;   // the PORTNUMBER we will be listening on
    private ServerSocketChannel serverChannel;  // the ServerSocketChannel we will be using
    private Selector selector;  // the selector we will be using
    private final LinkedBlockingQueue<SelectionKey> sendToClient = new LinkedBlockingQueue<>(); // the queue for sending

    /**
     * Sets up the selector
     * @throws IOException
     */
    public Server() throws IOException {
        this.selector = this.initSelector();    // initialise the selector
    }

    /**r
     * Server main method, run to run server
     * @param args not used
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new Server().run(); // start server
    }

    /**
     * Initialize the selector
     * @return a selector that has been set up
     * @throws IOException
     */
    private Selector initSelector() throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();   // opens a selector
        this.serverChannel = ServerSocketChannel.open();    // opens the serversocketchannel
        this.serverChannel.configureBlocking(false);    // set it to not block
        this.serverChannel.socket().bind( new InetSocketAddress( this.PORTNUMBER ) ); // bind it to PORTNUMBER
        this.serverChannel.register( socketSelector, SelectionKey.OP_ACCEPT );  // register with selector, yields a key
        return socketSelector;  // return the selector
    }

    /**
     * Sets up and registers a clienthandler for a new connection
     * @param key the key we want to use
     * @throws IOException
     */
    private void startClientHandler (SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();  // set up a serversocketchannel from key
        SocketChannel socketChannel = serverSocketChannel.accept(); // accepts the connection to this channels socket
        socketChannel.configureBlocking(false); // set the channel to not block

        ClientHandler handler = new ClientHandler(this, socketChannel); // start a new handler, pass server and the socketchannel
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ, handler);    // register the channel for reading with selector, attach specific handler
        handler.setKey(selectionKey);  // pass key to handler
    }

    /**
     * Write message to a client via a clienthandler
     * @param key the key we want to use
     * @throws IOException
     */
    private void writeToClient (SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment(); // grab the clienthander that is attached to the key
        clientHandler.writeMessage();   // write message to the client
        key.interestOps(SelectionKey.OP_READ);  // set key to be ready for reading out of
    }

    /**
     * Read message from a clienthandler
     * @param key the key we want to use
     * @throws IOException
     */
    private void readFromClient (SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment(); // grab the clienthandler that was attached
        try {
            clientHandler.readMessage();    // read the message from the clienthandler
        } catch (IOException e) {   // if something goes awry we kick the client belonging to the key into orbit
            disconnectClient(key);
        }
    }

    /**
     * Remove a client if necessary
     * @param key the key we want to use
     */
    private void disconnectClient (SelectionKey key) {
        ClientHandler clientHandler = (ClientHandler) key.attachment(); // grab the clienthandler that is attached to the key
        clientHandler.disconnectClient();   // disconnect it
        key.cancel();   // cancel the key
    }

    /**
     * Add a message to the sending queue
     * @param key the key we want to use
     */
    public void addToSendQueue(SelectionKey key) {
        sendToClient.add(key);  // add a key to the queue
    }

    /**
     * Wake the selector up to be able to do things after adding stuff to the queue
     */
    public void wakeupSelector() {
        selector.wakeup();  // wake the selector up
    }

    /**
     * Things we do continuously while running the program
     */
    public void run() {
        System.out.println("Server started, listening on PORTNUMBER: " + PORTNUMBER);   // just say that we started
        while (true) {
            try {
                while (!sendToClient.isEmpty()) {   // send any messages in the queue
                    sendToClient.poll().interestOps( SelectionKey.OP_WRITE );
                }

                this.selector.select(); // select available keys
                for (SelectionKey key : this.selector.selectedKeys()) { // for every key that we managed to select
                    if ( !key.isValid() ) { // if the key is invalid then we skip it
                        continue;
                    }
                    if ( key.isAcceptable() ) { // if the key's channel is ready to accept a new connection
                        startClientHandler(key);
                    }
                    else if ( key.isReadable() ) {  // if the key's channel is ready for reading
                        readFromClient(key);
                    }
                    else if ( key.isWritable() ) {  // if the key's channel is ready for writing
                        writeToClient(key);
                    }
                    selector.selectedKeys().remove(key);    // when done remove the key from the set
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
