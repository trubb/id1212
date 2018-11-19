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

import static shared.Message.deserialize;
import static shared.Message.serialize;

public class ClientHandler implements Runnable {
    private final Server server;    // a server instance that this belongs to
    private final SocketChannel clientChannel;  // a socketchannel that we will use
    private final ByteBuffer clientMessage = ByteBuffer.allocate(8192); // again 8192 seems reasonably large
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();  // a queue for reading messages
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();  // a queue for sending messages

    private SelectionKey selectionKey; // Selection key for this handler
    private HangManGame hangmanGame = new HangManGame();    // a game instance for this connection

    /**
     * Setup to point at the right instances
     * @param server the server that spawned this handler
     * @param clientChannel the channel that points to the relevant client
     */
    ClientHandler(Server server, SocketChannel clientChannel) {
        this.server = server;   // the server that we belong to
        this.clientChannel = clientChannel; // the socketchannel that we will be using
    }

    /**
     * Things to do continuously while running
     */
    @Override
    public void run() {
        Iterator<Message> iterator = readingQueue.iterator();   // create an iterator for the readingqueue
        while (iterator.hasNext()) {    // as long as there are items left in the iterator
            Message message = iterator.next();  // pick out a message from the iterator
            switch (message.getMessageType()) { // check what type of message it is
                case START: // if it is a START message then we start a game round
                    String currentState = this.hangmanGame.startRound();    // grab game state
                    System.out.println( message.getMessage() );   // print the user input so we see what is going on
                    sendResponseToClient( MessageType.START_RESPONSE, currentState ); // send the game state to the client
                    break;
                case GUESS: // if the user sent us a GUESS then we pass it to the game
                    String currentState1 = this.hangmanGame.guessParser( message.getMessage() );
                    System.out.println( message.getMessage() ); // print the user input so we see what is going on
                    if (this.hangmanGame.getWord() == null) {   // if the word is set to null (round ended)
                        sendResponseToClient( MessageType.END_RESPONSE, currentState1 );    // send an END state
                    } else {
                        sendResponseToClient( MessageType.GUESS_RESPONSE, currentState1 );  // else just send game state
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

    /**
     * Read message from the client channel buffer
     * @throws IOException
     */
    void readMessage() throws IOException {
        clientMessage.clear();  // clear the buffer
        int numOfReadBytes = clientChannel.read(clientMessage); // check how many bytes have been read
        if (numOfReadBytes == -1) throw new IOException("Client has closed connection.");   // if -1 we've reached the end

        readingQueue.add( deserialize( extractMessageFromBuffer() ) );  // pick apart the message and add it to the reading queue
        ForkJoinPool.commonPool().execute(this);    // spawn a separate thread to deal with this
    }

    /**
     * Write message to the client
     * @throws IOException
     */
    void writeMessage() throws IOException {
        synchronized (sendingQueue) {   // so that things happen in an orderly manner
            while (sendingQueue.size() > 0) {   // as long as the sendingQueue is not empty
                // write from the sendingqueue into this buffer
                ByteBuffer message = ByteBuffer.wrap( serialize( sendingQueue.poll() ).getBytes() );
                clientChannel.write(message);   // write to the client through the channel
            }
        }
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
     * @param selectionKey
     */
    void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    /**
     * Extract a message from the client out of the buffer
     * @return
     */
    private String extractMessageFromBuffer() {
        clientMessage.flip();   // flip the buffer to prep for reading
        byte[] bytes = new byte[ clientMessage.remaining() ];   // grab the bytes from the buffer
        clientMessage.get(bytes);   // get the content
        return new String(bytes);   // return it as a string
    }

    /**
     * Create a message and add it to the queue of messages to be sent
     * @param messageType
     * @param body
     */
    private void sendResponseToClient(MessageType messageType, String body) {
        Message message = new Message(messageType, body);   // create a new message

        synchronized (sendingQueue) {   // so that we do this in an orderly fashion
            sendingQueue.add(message);  // add the message to the sendingQueue
        }

        server.addMessageToWritingQueue(this.selectionKey); // add the message to the servers queue by handing it our key
        server.wakeupSelector();    // wake up the selector
    }
}