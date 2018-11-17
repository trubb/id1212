package server.net;

import server.controller.Controller;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClientHandler {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Controller controller;

    public ClientHandler ( ServerSocketChannel serverSocketChannel, Selector selector ) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    public void start() {

        while (true) {
            try {
                // initialize selector
                // initialize socketchannelthing

                while (true) {

                    selector.select();
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

            } catch (Exception e) {
                System.err.println("Clienthandler made an oopsie");
            }
        }

    }

    private void acceptConnection (SelectionKey key) throws IOException {
        SocketChannel socketChannel;
        Socket socket;
        socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socket = socketChannel.socket();
        System.out.println("New connection on " + socket);
        socketChannel.register(selector, SelectionKey.OP_READ);
        selector.selectedKeys().remove(key);
    }

    private void acceptData(SelectionKey key) throws IOException {

        SocketChannel socketChannel;
        Socket socket;

        // set up a buffer
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.clear(); // necessary? probably not

        socketChannel = (SocketChannel) key.channel();
        int numberOfReadBytes = socketChannel.read(buffer);
        System.out.println("number of bytes: " + numberOfReadBytes);
        socket = socketChannel.socket();

        String input = readMessage(buffer);
        System.out.println("server received following message: " + input);

        if (numberOfReadBytes == -1) {
            key.cancel();
            System.out.println("Client probably closed connection, closing socket " + socket);
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Unable to close socket");
            }
        } else {
            try {
                // reading from a buffer requires flipping it first
                buffer.flip();
                while ( buffer.hasRemaining() ) {
                    socketChannel.write(buffer);
                }
            } catch (IOException e) {
                System.err.println("Closing socket " + socket);
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ioe) {
                    System.err.println("Unable to close socket");
                }
            }
        }

        // start a new game
        controller = new Controller( socketChannel, socket, buffer );
        controller.init();
        controller.run( readMessage( buffer ) );

        // remove key as it has been handled
        selector.selectedKeys().remove(key);

    }

    private String readMessage (ByteBuffer buffer) {
        buffer.flip();  // gotta flip to read
        byte[] bytes = new byte[ buffer.remaining() ];
        buffer.get(bytes);
        return new String(bytes);
    }

}
