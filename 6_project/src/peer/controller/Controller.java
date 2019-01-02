package peer.controller;

import common.FormattedPrinter;
import common.PeerTable;
import peer.net.server.ControllerObserver;
import peer.net.server.OutputHandler;
import peer.net.server.PeerInfo;
import peer.net.server.PeerServer;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    private static final Logger LOGGER = Logger.getLogger( Controller.class.getName() );
    private PeerInfo currentPeerInfo;
    private PeerTable peerTable;
    private OutputHandler console;
    private String startServerHost = "127.0.0.1";
    private int startServerPort = 48921;

    public Controller (OutputHandler console) {
        this.console = console;
        try {
            ServerSocket serverSocket = new ServerSocket(0);    // autoassign port
            this.currentPeerInfo = new PeerInfo( serverSocket.getInetAddress().getHostName(), serverSocket.getLocalPort() );
            new PeerServer().start( serverSocket, new PeerTableManipulator() );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }


    // TODO THESE
    public void joinNetwork ( String IP, int PORT ) {}

    private void contactJoinAllPeers() {}

    public void sendMove ( String move, OutputHandler console ) {}

    public void leaveNetwork() {}


    private class PeerTableManipulator implements ControllerObserver {

        @Override
        public void addPeer(PeerInfo peer) {
            peerTable.addPeerToTable(peer);
        }

        @Override
        public void removePeer(PeerInfo peer) {
            peerTable.removePeerFromTable( peer.getID() );

            String message = GameManager.checkEndGame( peerTable, currentPeerInfo );
            if ( !message.equals( "" ) ) {
                console.handleMsg( FormattedPrinter.buildScoreMessage( message ) );
            }
        }

        @Override
        public PeerInfo getPeerInfo() {
            return currentPeerInfo;
        }

        @Override
        public void setPeerMove(String move, PeerInfo peer) {
            peerTable.setPeerMove( peer, move );

            String message = GameManager.checkEndGame( peerTable, currentPeerInfo );
            if ( !message.equals( "" ) ) {
                console.handleMsg( FormattedPrinter.buildScoreMessage( message ) );
            }
        }
    }

}
