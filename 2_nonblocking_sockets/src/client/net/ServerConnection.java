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

    private final static String HOSTNAME = "127.0.0.1";
    private static int PORTNUMBER = 8080;//48922;
    private final ByteBuffer serverMessage = ByteBuffer.allocate(8192); // TODO - fault place
    private final LinkedBlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> readingQueue = new LinkedBlockingQueue<>();
    private CommunicationListener viewListener;
    private volatile boolean timeToSend = false;
    private boolean connected = false;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;

    public void connect () {
        this.serverAddress = new InetSocketAddress( HOSTNAME, PORTNUMBER );
        new Thread(this).start();
    }

    private void addToSendQueue (MessageType type, String message ) {
        Message messageToSend = new Message( type, message );
        synchronized (sendingQueue) {
            sendingQueue.add(messageToSend);
        }
        this.timeToSend = true;
        selector.wakeup();
    }

    public void startNewRound() {
        addToSendQueue( MessageType.START, "" );
    }

    public void submitGuess (String guess) {
        addToSendQueue( MessageType.GUESS, guess);
    }

    public void disconnect() throws IOException {
        this.connected = false;
        addToSendQueue(MessageType.QUIT, "");
        this.socketChannel.close();
        this.socketChannel.keyFor(selector).cancel();
    }

    public void setViewListener(CommunicationListener listener ) {
        this.viewListener = listener;
    }

    private void initSelector() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();
        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.connect(serverAddress);
        this.socketChannel.register( selector, SelectionKey.OP_CONNECT );
        this.connected = true;
    }

    private void connect (SelectionKey key) throws IOException {
        this.socketChannel.finishConnect();
        viewListener.print( MessagePrinter.gameStartInfo() );
        key.interestOps(SelectionKey.OP_WRITE); // TODO - OP_CONNECT????
    }

    private void writeToServer (SelectionKey key) throws IOException {
        synchronized (sendingQueue) {
            while (sendingQueue.size() > 0) {
                ByteBuffer message = ByteBuffer.wrap( serialize( sendingQueue.poll() ).getBytes() );
                socketChannel.write(message);
                if (message.hasRemaining()) {
                    return;
                }
            }
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private String extractMessageFromBuffer() {
        serverMessage.flip();
        byte[] bytes = new byte[ serverMessage.remaining() ];
        serverMessage.get(bytes);
        return new String(bytes);
    }

    private void readFromServer (SelectionKey key) throws IOException {
        serverMessage.clear();  // clear buffer so we know it's empty
        int numOfReadBytes = socketChannel.read(serverMessage);
        if (numOfReadBytes == -1) throw new IOException("Client closed connection");
        readingQueue.add( deserialize( extractMessageFromBuffer() ) );

        while (readingQueue.size() > 0) {
            Message message = readingQueue.poll();

            switch (message.getMessageType()) {
                case START_RESPONSE:
                    viewListener.print( MessagePrinter.startGuess( message.getMessage() ) );
                    break;
                case GUESS_RESPONSE:
                    viewListener.print( MessagePrinter.guessReply( message.getMessage() ) );
                    break;
                case END_RESPONSE:
                    viewListener.print( MessagePrinter.endReply( message.getMessage() ) );
                    break;
                default:
                    viewListener.print(message.getMessage());
            }
        }
    }

    @Override
    public void run() {
        try {
            initSelector();

            while (connected) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                this.selector.select();
                for (SelectionKey key : this.selector.selectedKeys()) {
                    if ( !key.isValid() ) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        connect(key);
                    } else if (key.isReadable()) {
                        readFromServer(key);
                    } else if (key.isWritable()) {
                        writeToServer(key);
                    }
                    selector.selectedKeys().remove(key);
                }
            }
        } catch (Exception e) {

        }
    }

}
