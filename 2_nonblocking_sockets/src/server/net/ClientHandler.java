package server.net;

import shared.Message;
import shared.MessageType;
import server.model.HangManGame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import static shared.Message.deserializeMessage;
import static shared.Message.serializeMessage;

public class ClientHandler implements Runnable {

    private final Server server;    // the server instance that this handler belongs to
    private final SocketChannel clientChannel;  // a socketchannel that we will use
    private final ByteBuffer clientMessage = ByteBuffer.allocate(8192); // again 8192 seems reasonably large
    private final LinkedBlockingQueue<Message> readQueue = new LinkedBlockingQueue<>();  // a queue for reading messages
    private final LinkedBlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();  // a queue for sending messages

    private SelectionKey selectionKey; // Selection key for this handler
    private HangManGame hangmanGame = new HangManGame();    // a game instance for this connection

    /**
     * Setup to point at the right instances
     * @param server the server that spawned this handler
     * @param clientChannel the channel that points to the relevant client
     */
    ClientHandler ( Server server, SocketChannel clientChannel ) {
        this.server = server;   // the server that we belong to
        this.clientChannel = clientChannel; // the socketchannel that we will be using
    }

    /**
     * Write message to the client
     * @throws IOException
     */
    void writeMessage() throws IOException {
        synchronized (sendQueue) {   // so that things happen in an orderly manner
            while ( sendQueue.size() > 0 ) {   // as long as the sendQueue is not empty
                // write from the sendingqueue into this buffer
                ByteBuffer message = ByteBuffer.wrap( serializeMessage( sendQueue.poll() ).getBytes() );
                clientChannel.write(message);   // write to the client through the channel
            }
        }
    }

    /**
     * Create a message and add it to the queue of messages to be sent
     * @param type the type of message to be sent
     * @param body the content of the message
     */
    private void sendAnswerToClient ( MessageType type, String body ) {
        Message message = new Message( type, body );   // create a new message

        synchronized (sendQueue) {   // so that we do this in an orderly fashion
            sendQueue.add(message);  // add the message to the sendQueue
        }

        server.addToSendQueue( this.selectionKey ); // add the message to the servers queue by handing it our key
        server.wakeupSelector();    // wake up the selector
    }

    /**
     * Read message from the client channel buffer
     * @throws IOException
     */
    void readMessage() throws IOException {
        clientMessage.clear();  // clear the buffer
        int numOfReadBytes = clientChannel.read(clientMessage); // check how many bytes have been read
        if (numOfReadBytes == -1) throw new IOException("Client has closed connection.");   // if -1 we've reached the end

        readQueue.add( deserializeMessage( readFromClientBuffer() ) );  // pick apart the message and add it to the reading queue
        ForkJoinPool.commonPool().execute(this);    // spawn a separate thread to deal with this
    }

    /**
     * Disconnects the client
     */
    void disconnectClient() {
        try {
            clientChannel.close();  // close the channel the client used
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the selectionkey for this handler so that we know which client this is
     * @param key the key we want to use
     */
    void setKey (SelectionKey key) {
        this.selectionKey = key;
    }

    /**
     * Extract a message from the client out of the buffer
     * @return the content of the buffer as a string
     */
    private String readFromClientBuffer() {
        clientMessage.flip();   // flip the buffer to prep for reading
        byte[] bytes = new byte[ clientMessage.remaining() ];   // grab the bytes from the buffer
        clientMessage.get(bytes);   // get the content
        return new String(bytes);   // return it as a string
    }

    /**
     * Things to do continuously while running
     */
    @Override
    public void run() {
        Iterator<Message> iterator = readQueue.iterator();   // create an iterator for the readingqueue
        while (iterator.hasNext()) {    // as long as there are items left in the iterator
            Message message = iterator.next();  // pick out a message from the iterator
            switch (message.getMessageType()) { // check what type of message it is
                case START: // if it is a START message then we start a game round
                    String gameStartState = this.hangmanGame.startRound();    // grab game state
                    System.out.println( message.getMessageText() );   // print the user input so we see what is going on
                    sendAnswerToClient( MessageType.START_RESPONSE, gameStartState ); // send the game state to the client
                    break;
                case GUESS: // if the user sent us a GUESS then we pass it to the game
                    String gameGuessState = this.hangmanGame.guessParser( message.getMessageText() );
                    System.out.println( message.getMessageText() ); // print the user input so we see what is going on
                    if (this.hangmanGame.getWord() == null) {   // if the word is set to null (round ended)
                        sendAnswerToClient( MessageType.END_RESPONSE, gameGuessState );    // send an END state
                    } else {
                        sendAnswerToClient( MessageType.GUESS_RESPONSE, gameGuessState );  // else just send game state
                    }
                    break;
                case QUIT:  // if the client wants to quit then we disconnect
                    disconnectClient(); // disconnects the client
                    break;
                default:
                    System.out.println("Message type unavailable!");    // if something went awry
            }
            iterator.remove();  // remove the item from the iterator
        }
    }

}