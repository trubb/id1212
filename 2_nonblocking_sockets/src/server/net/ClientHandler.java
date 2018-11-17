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

    /**
     * Set up the ClientHandler so that we use the correct ServerSocketChannel and Selector
     * @param serverSocketChannel the ServerSocketChannel that was created by the Server
     * @param selector the Selector created by the server
     */
    public ClientHandler ( ServerSocketChannel serverSocketChannel, Selector selector ) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    /**
     * Continuously deal with input
     */
    public void processConnections() {
        while (true) {
            try {
                // Selects a set of keys whose corresponding channels are ready for I/O operations.
                selector.select();
                // Returns an iterator over this selector's selected-key set
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                // As long as there are keys left in the iterator
                while (iterator.hasNext()) {
                    // Set key to be the next (current?) key of the iterator and removes it from there
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    // We are interested in two operations from a client: connecting and taking input
                    // Check what they key is for and do that
                    if (key.isAcceptable()) {
                        // Tests whether this key's channel is ready to accept a new socket connection.
                        // if true then we accept it by invoking acceptConnection
                        acceptConnection(key);
                    } else if (key.isReadable()) {
                        //Tests whether this key's channel is ready for reading.
                        // if true we accept it by invoking acceptData
                        acceptData(key);
                    }
                }
            } catch (Exception e) {
                System.err.println("Clienthandler fucked up");
            }
        }
    }

    /**
     * Accepts a connection from a client
     * @param key the key for the connection
     * @throws IOException if something goes wrong
     */
    private void acceptConnection (SelectionKey key) throws IOException {
        // Setup
        SocketChannel socketChannel;
        Socket socket;
        socketChannel = serverSocketChannel.accept();
        // Set nonblocking and point socket at socketChannel's Socket
        socketChannel.configureBlocking(false);
        socket = socketChannel.socket();
        //Server console message
        System.out.println("New connection on " + socket);
        // Registers this channel with the given selector, returning a selection key whose value is set to 1 (OP_READ)
        socketChannel.register(selector, SelectionKey.OP_READ);
        // Remove the key from the Set selectedKeys
        selector.selectedKeys().remove(key);
    }

    /**
     * Accepts data - guesses and commands - from a client
     * @param key the key for the connection
     * @throws IOException if something goes wrong
     */
    private void acceptData(SelectionKey key) throws IOException {
        // set up a socketchannel
        SocketChannel socketChannel;
        Socket socket;

        // Set up a buffer with capacity 2048, seems like a reasonable max
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        // Clears this buffer: position set to zero, limit set to capacity, mark discarded
        buffer.clear(); // necessary? possibly not, lets do anyway

        // Set socketchannel to the channel of the Key
        socketChannel = (SocketChannel) key.channel();
        // Read a sequence of bytes from socketChannel into the buffer, returns the n of read bytes
        // The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
        int numberOfReadBytes = socketChannel.read(buffer);
        // Print what was read
        System.out.println("number of bytes: " + numberOfReadBytes);
        // Point the socket at the socketChannel's Socket
        socket = socketChannel.socket();
        // Read the buffer and put it in a string
        String input = readMessage(buffer);
        // Print said string
        System.out.println("server received following message: " + input);

        // If we find EOF cancel the key, we're probably done, close socket
        if (numberOfReadBytes == -1) {
            key.cancel();
            System.out.println("Client probably closed connection, closing socket " + socket);
            socketClose(socket);
        } else {
            try {
                // Reading from a buffer requires flipping it first
                buffer.flip();
                // As long as there is something left in the buffer, keep writing to the SocketChannel
                while ( buffer.hasRemaining() ) {
                    socketChannel.write(buffer);
                }
            } catch (IOException e) {
                System.err.println("Closing socket " + socket);
                socketClose(socket);
            }
        }

        // Create a new controller
        controller = new Controller( socketChannel, socket, buffer );
        controller.init();
        controller.run( readMessage( buffer ) );

        // Remove the key from the Set selectedKeys as it has been handled
        selector.selectedKeys().remove(key);
    }

    /**
     * Read a message from a buffer
     * @param buffer the buffer we want to read from
     * @return
     */
    private String readMessage ( ByteBuffer buffer ) {
        // Flip (reset) the buffer to read from it
        buffer.flip();
        // Create an array big enough to house the whole buffer (current -> limit)
        byte[] bytes = new byte[ buffer.remaining() ];
        // Put the content of the buffer into the array
        buffer.get(bytes);
        // Return the content of the buffer as a string
        return new String(bytes);
    }

    /**
     * Helper to close sockets instead of having the try-catch block take up space elsewhere
     * @param socket the socket that shall be closed
     */
    private void socketClose ( Socket socket ) {
        try {
            if ( socket != null ) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close socket");
        }
    }

}
