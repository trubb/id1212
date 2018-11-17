package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server {

    private static final int PORTNUMBER = 48922;
    private static ServerSocketChannel serverSocketChannel;
    private static Selector selector;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        ServerSocket serverSocket;
        System.out.println("Server started on port " + PORTNUMBER);
        try {
            // open the serversocketchannel and set it to not block
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // set the serversocket to point to serversocketchannel's socket
            serverSocket = serverSocketChannel.socket();

            // designate a new socket adress
            InetSocketAddress ipaddress = new InetSocketAddress(PORTNUMBER);
            serverSocket.bind(ipaddress);

            // open the selector, and register the ServerSocketChannel with it
            // for receiving connections from clients
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // create and processConnections a client handler, pass it the serversocketchannel and selector
        ClientHandler clientHandler = new ClientHandler(serverSocketChannel, selector);
        clientHandler.processConnections();

    }

}
