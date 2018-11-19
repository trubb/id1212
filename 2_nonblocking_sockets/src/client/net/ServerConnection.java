package client.net;

import shared.Message;
import shared.MessageType;
import shared.MessagePrinter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

import static shared.Message.deserialize;
import static shared.Message.serialize;

public class ServerConnection implements Runnable {

    private final static String HOSTNAME = "127.0.0.1"; // It's easier to always use localhost in this case
    private static int PORTNUMBER = 48922;  // hopefully un(der)used port
    private final ByteBuffer serverMessage = ByteBuffer.allocate(8192); // 8192 seems like a reasonably large buffersize
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();  // queue for sending
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();  // queue for receiving
    private CommunicationListener viewListener;
    private volatile boolean timeToSend = false;
    private boolean connected = false;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;

    /**
     * Connect to the server and start our run method in a thread
     */
    public void connect () {
        this.serverAddress = new InetSocketAddress( HOSTNAME, PORTNUMBER );
        new Thread(this).start();
    }

    /**
     * Add a message to the sending queue
     * @param type the message we want to send
     * @param message what type of message it is that we want to send
     */
    private void addToSendQueue (MessageType type, String message ) {
        Message messageToSend = new Message( type, message );   // create a new Message of specified type
        synchronized (sendingQueue) {   // make sure that this can only be done one at a time
            sendingQueue.add(messageToSend);    // add the message to the queue
        }
        this.timeToSend = true; // show that we want to send a message
        selector.wakeup();  // wake up the selector
    }

    /**
     * Starts a new game round by passing a empty message of type START
     */
    public void startNewRound() {
        addToSendQueue( MessageType.START, "" );
    }

    /**
     * Send a guess by passing the guess as a GUESS type message
     * @param guess the string of length 1 - n we want to guess
     */
    public void submitGuess (String guess) {
        addToSendQueue( MessageType.GUESS, guess);
    }

    /**
     * Disconnect from the server
     * @throws IOException
     */
    public void disconnect() throws IOException {
        this.connected = false; // we're not connected anymore so this is false
        addToSendQueue(MessageType.QUIT, "");   // We'd like to show that we're disconnected so send QUIT
        this.socketChannel.close(); // close the socketChannel as it won't be used anymore
        this.socketChannel.keyFor(selector).cancel();   // And lastly cancel the key
    }

    /**
     * Sets the communicationlistener of this instance
     * @param listener the communicationlistener we shall use
     */
    public void setViewListener(CommunicationListener listener ) {
        this.viewListener = listener;
    }

    /**
     * Setup selector and socketchannel
     * @throws IOException
     */
    private void initSelector() throws IOException {
        this.selector = SelectorProvider.provider().openSelector(); // opens a selector
        this.socketChannel = SocketChannel.open();      // opens the socketchannel
        this.socketChannel.configureBlocking(false);    // set it to not block
        this.socketChannel.connect(serverAddress);  // connect it to the servers
        this.socketChannel.register( selector, SelectionKey.OP_CONNECT );   // register with selector, returns a selectionkey
        this.connected = true;  // now we're connected
    }

    /**
     * Finishes the connection to the server and states to the user that we're up and running
     * @param key the key we want to use
     * @throws IOException
     */
    private void connect (SelectionKey key) throws IOException {
        this.socketChannel.finishConnect();
        viewListener.print( MessagePrinter.clientStartInfo() );
        key.interestOps(SelectionKey.OP_WRITE);
    }

    /**
     * Writes a message to the server
     * @param key the key to be used
     * @throws IOException
     */
    private void writeToServer (SelectionKey key) throws IOException {
        synchronized (sendingQueue) {   // so that this can be done only one at a time
            while (sendingQueue.size() > 0) {   // as long as there are items in the queue we keep sending
                ByteBuffer message = ByteBuffer.wrap( serialize( sendingQueue.poll() ).getBytes() );
                socketChannel.write(message);   // write to the socketchannel from the buffer until there is nothing left
                if (message.hasRemaining()) {
                    return;
                }
            }
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * Extract a message from the server out of the buffer
     * @return the message
     */
    private String extractMessageFromBuffer() {
        serverMessage.flip();  // flip the buffer to be able to read from it
        byte[] bytes = new byte[ serverMessage.remaining() ];   // grab the bytes from the buffer
        serverMessage.get(bytes);   // get the content
        return new String(bytes);   // return it as a string
    }

    /**
     * Read messages from the server
     * @param key the key that we want to use
     * @throws IOException
     */
    private void readFromServer (SelectionKey key) throws IOException {
        serverMessage.clear();  // clear buffer so we know it's empty
        int numOfReadBytes = socketChannel.read(serverMessage); // check how many bytes have been read
        if (numOfReadBytes == -1) throw new IOException("closed connection");   // If -1 we've reached the end
        readingQueue.add( deserialize( extractMessageFromBuffer() ) );  // att message to reading queue

        while (readingQueue.size() > 0) {   // as long as there are items in the queue
            Message message = readingQueue.poll();  // pull message from the queue

            switch (message.getMessageType()) {
                case START_RESPONSE:    // if the message is of type START_RESPONSE print a startGuess message
                    viewListener.print( MessagePrinter.startGuess( message.getMessage() ) );
                    break;
                case GUESS_RESPONSE:    // if the message is of type GUESS_RESPONSE print a guessReply message
                    viewListener.print( MessagePrinter.guessReply( message.getMessage() ) );
                    break;
                case END_RESPONSE:  // if the message is of type END_RESPONSE the round ended, print endReply
                    viewListener.print( MessagePrinter.endReply( message.getMessage() ) );
                    break;
                default:    // else print the retreived message's contents plainly
                    viewListener.print(message.getMessage());
            }
        }
    }

    /**
     * This is the things we do continuously while this thread is running
     */
    @Override
    public void run() {
        try {
            initSelector(); // setup the connection

            while (connected) { // as long as we are connected
                if (timeToSend) {   // if we should send a message to the server
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);  // state that we want to write
                    timeToSend = false; // then it is no longer time to write
                }

                this.selector.select(); // selects the keys that are ready for IO operation
                for (SelectionKey key : this.selector.selectedKeys()) { // for each key that has been selected
                    if ( !key.isValid() ) { // if it is invalid then skip it
                        continue;
                    }
                    if (key.isConnectable()) {  // if the key is connectable then we connect to the server
                        connect(key);
                    } else if (key.isReadable()) {  // if the key can be read we read messages from the server
                        readFromServer(key);
                    } else if (key.isWritable()) {  // if the key is writeable then we send messages to the server
                        writeToServer(key);
                    }
                    selector.selectedKeys().remove(key);    // remove the key when we're done processing it
                }
            }
        } catch (Exception e) {

        }
    }

}
