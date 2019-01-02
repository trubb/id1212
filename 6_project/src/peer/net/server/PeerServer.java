package peer.net.server;

import peer.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;

public class PeerServer implements Runnable {

    private ServerSocket serverSocket;
    private ControllerObserver controllerObserver;

    public void start (ServerSocket serverSocket, ControllerObserver controllerObserver) {
        this.serverSocket = serverSocket;
        this.controllerObserver = controllerObserver;
        new Thread( this ).start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                new PeerClientHandler(serverSocket.accept(), controllerObserver).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
