package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerConnection implements Runnable {

    private SocketChannel socketChannel;
    private final LinkedBlockingQueue<String> sendingQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> readingQueue = new LinkedBlockingQueue<>();
    private InetSocketAddress serverAddress;
    private boolean connected;
    private Socket socket;
    private PrintWriter messageToServer;
    private BufferedReader messageFromServer;
    private Selector selector;
    private CommunicationListener communicationListener;
    private boolean timeToSend;
    private final ByteBuffer serverMessage = ByteBuffer.allocateDirect(4096);

    public void initSelector() throws Exception {
        this.selector = SelectorProvider.provider().openSelector();
        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.connect(serverAddress);
        this.socketChannel.register( selector, SelectionKey.OP_CONNECT);
        this.connected = true;
    }

    public void connect ( String HOSTNAME, int PORTNUMBER ) throws IOException {
        this.serverAddress = new InetSocketAddress( HOSTNAME, PORTNUMBER );
        new Thread(this).start();
    }

    public void disconnect() throws IOException {
        try {
            this.connected = false;
            sendMessage("Closing connection");
            this.socketChannel.close();
            this.socketChannel.keyFor(selector).cancel();

            System.out.println("Closing connection");
        } catch (IOException e) {
            System.err.println("Couldnt disconnect");
            System.exit(1);
        }
    }

    public void sendMessage ( String message ) throws IOException {
        synchronized (sendingQueue) {
            sendingQueue.add( message );
        }

        this.timeToSend = true;
        selector.wakeup();
    }

    public void setCommunicationListener ( CommunicationListener communicationListener ) {
        this.communicationListener = communicationListener;
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

                /*
                while (true) {
                    this.selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isAcceptable()) {
                            acceptConnection(key);
                        } else if (key.isReadable()) {
                            acceptData(key);
                        }
                    }
                }
                */

                this.selector.select();

                for ( SelectionKey key : this.selector.selectedKeys() ) {
                    if ( !key.isValid() ) {
                        continue;
                    }
                    if ( key.isConnectable() ) {
                        establishConnection(key);
                    } else if ( key.isReadable() ) {
                        readFromServer(key);
                    } else if ( key.isWritable() ) {
                        writeToServer(key);
                    }

                    selector.selectedKeys().remove(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void establishConnection ( SelectionKey key ) throws Exception {
        this.socketChannel.finishConnect();
        communicationListener.print("Are you ready?");
        key.interestOps( SelectionKey.OP_WRITE );
    }

    private void readFromServer ( SelectionKey key ) throws Exception {
        serverMessage.clear();
        int numberOfReadBytes = socketChannel.read( serverMessage );
        readingQueue.add( extractMessageFromBuffer() );

        while ( readingQueue.size() > 0 ) {
            String message = readingQueue.poll();
            communicationListener.print( message );
        }
    }

    private void writeToServer ( SelectionKey key ) throws Exception {
        synchronized (sendingQueue) {
            while ( sendingQueue.size() > 0 ) {
                ByteBuffer message = ByteBuffer.wrap( sendingQueue.poll().getBytes() );
                socketChannel.write( message );
                if ( message.hasRemaining() ) {
                    return;
                }
            }
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private String extractMessageFromBuffer() {
        serverMessage.flip();
        byte[] bytes = new byte[ serverMessage.remaining() ];
        serverMessage.get( bytes );
        return new String( bytes );
    }

}
