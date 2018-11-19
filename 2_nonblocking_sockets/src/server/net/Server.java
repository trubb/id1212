package server.net;
/*
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private final LinkedBlockingQueue<SelectionKey> sendingQueue = new LinkedBlockingQueue<>();
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private static final int PORTNUMBER = 48922;

    public static void main(String[] args) throws IOException {
        new Server().run();
    }

    public Server () throws IOException {
        this.selector = this.initSelector();
    }

    private Selector initSelector() throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.socket().bind( new InetSocketAddress( PORTNUMBER ) );
        this.serverSocketChannel.register( socketSelector, SelectionKey.OP_ACCEPT );

        return socketSelector;
    }

    public void run() {
        System.out.println("Server started, listening on port: " + PORTNUMBER);
        while (true) {
            try {
                while (!sendingQueue.isEmpty()) {
                    sendingQueue.poll().interestOps(SelectionKey.OP_WRITE);
                }
                this.selector.select();

                for ( SelectionKey key : this.selector.selectedKeys() ) {
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    } else if (key.isReadable()) {
                        readFromClient(key);
                    } else if (key.isWritable()) {
                        writeToClient(key);
                    }
                    selector.selectedKeys().remove(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startHandler (SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        ClientHandler handler = new ClientHandler(this, socketChannel);
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ, handler);
        handler.setSelectionKey(selectionKey);
    }

    private void readFromClient (SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        try {
            clientHandler.readMessage();
        } catch (IOException e) {
            removeClient(key);
        }
    }

    private void writeToClient (SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.writeMessage();
        key.interestOps(SelectionKey.OP_READ);
    }

    private void removeClient (SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.disconnectClient();
        key.cancel();
    }

    public void addMessageToWritingQueue (SelectionKey key) {
        sendingQueue.add(key);
    }

    public void wakeUpSelector() {
        selector.wakeup();
    }
}
*/

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private final LinkedBlockingQueue<SelectionKey> sendingQueue = new LinkedBlockingQueue<>();
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private int port;

    public Server(int port) throws IOException {
        this.port = port;
        this.selector = this.initSelector();
    }

    public static void main(String[] args) throws IOException {
        int portNum = args.length == 1 ? Integer.parseInt(args[0]) : 8080;
        new Server(portNum).run();
    }

    private Selector initSelector() throws IOException {
        Selector socketSelector = SelectorProvider.provider().openSelector();

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.serverChannel.socket().bind(new InetSocketAddress(this.port));
        this.serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    public void run() {
        while (true) {
            try {
                // First send enqueued messages
                while (!sendingQueue.isEmpty()) {
                    sendingQueue.poll().interestOps(SelectionKey.OP_WRITE);
                }

                // Then check if there are selection keys available
                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) startHandler(key);
                    else if (key.isReadable()) readFromClient(key);
                    else if (key.isWritable()) writeToClient(key);

                    selector.selectedKeys().remove(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        ClientHandler handler = new ClientHandler(this, socketChannel);
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ, handler);
        handler.setSelectionKey(selectionKey);
    }

    private void readFromClient(SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        try {
            clientHandler.readMessage();
        } catch (IOException e) {
            removeClient(key);
        }
    }

    private void writeToClient(SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.writeMessage();
        key.interestOps(SelectionKey.OP_READ);
    }

    private void removeClient(SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.disconnectClient();
        key.cancel();
    }

    public void addMessageToWritingQueue(SelectionKey selectionKey) {
        sendingQueue.add(selectionKey);
    }

    // Wake up selector after enqueueing messages from other classes
    public void wakeupSelector() {
        selector.wakeup();
    }
}