package peer.controller;

import common.PreparedMessages;
import common.PeerList;
import peer.net.client.PeerConnection;
import peer.net.client.TrackerConnection;
import peer.net.server.ControllerObserver;
import peer.net.server.OutputHandler;
import peer.net.server.PeerInfo;
import peer.net.server.PeerServer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The controller for the peer client
 */
public class Controller {

    private static final Logger LOGGER = Logger.getLogger( Controller.class.getName() );
    private PeerInfo currentPeerInfo;   // information of the local peer ( " me " )
    private PeerList peerList;        // list of peers
    private OutputHandler console;      // console output that we will be using, passed by caller
    private String trackerHost;   // the IP of the "tracker" that we will check in with
    private int trackerPort;            // the port of the "tracker" that we will check in with

    /**
     * Constructor for the Controller
     * @param console   The output console that the controller will be using for output
     */
    public Controller ( OutputHandler console ) {

        this.console = console; // set controller's console to the passed one
        try {
            ServerSocket serverSocket = new ServerSocket( 0 );    // autoassign port for the serversocket so they do not collide
            // Set this client's PeerInfo to the ip and port of the newly created ServerSocket
            // Other clients (peers) will use this information when connecting to this client as stated by the p2p model
            this.currentPeerInfo = new PeerInfo( serverSocket.getInetAddress().getHostName(), serverSocket.getLocalPort() );
            // create a PeerServer which will deal with connections from peers
            new PeerServer().start( serverSocket, new PeerTableManipulator() );
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Join a network at the specified IP and PORT
     * @param IP        the IP we want to connect to
     * @param PORT      the port we want to connect to
     */
    public void joinNetwork ( String IP, int PORT ) {

        this.trackerHost = IP;  // passed ip is the IP we will connect to
        this.trackerPort = PORT;    // passed port is the PORT we will connect to
        CompletableFuture.runAsync( () -> { // run the connection separately to avoid blocking
            try {
                // create a new connection to the "tracker"
                TrackerConnection trackerConnection = new TrackerConnection();
                trackerConnection.startConnection( IP, PORT );    // connect to the provided IP and PORT
                // let the "tracker" know that we are connecting and that we want to know what other clients are in the net
                peerList = trackerConnection.sendJoinMessage( currentPeerInfo );
                // disconnect from the tracker, we have the list of peers now
                trackerConnection.stopConnection();
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            } catch (IOException e) {
                console.handleMsg( PreparedMessages.networkErrorMessage( "unable to connect to start server" ) );
            }
        }).thenRun(
                // when done with the previous task we print how many peers are present in the PeerList, and by extension in the net
            () -> console.handleMsg(
                PreparedMessages.onConnectMessage( peerList.getTableSize() )
            )
        ).thenRun(this::contactJoinAllPeers);   // run contactJoinAllPeers which is present in <this> class
                // (<CLASS>::<METHOD>)
    }

    /**
     * Disconnect from the net of peers
     */
    public void leaveNetwork() {
        CompletableFuture.runAsync( () -> { // avoid blocking
            try {
                // connect to the tracker
                TrackerConnection trackerConnection = new TrackerConnection();
                trackerConnection.startConnection(trackerHost, trackerPort);
                trackerConnection.sendLeaveMessage( currentPeerInfo );    // tell it that we're leaving
                trackerConnection.stopConnection();   // disconnect

                // for every peer in our list let them know we're leaving
                for ( PeerInfo peer : peerList.getPeerInfo() ) {
                    PeerConnection peerConnection = new PeerConnection();
                    peerConnection.startConnection( peer.getHOST(), peer.getPORT() );
                    peerConnection.sendLeaveMessage( currentPeerInfo );
                    peerConnection.stopConnection();
                }
            } catch ( IOException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        });
    }

    /**
     * Connects to all the peers in the PeerList that we retrieved from the tracker server
     */
    private void contactJoinAllPeers() {

        for (PeerInfo peer : peerList.getPeerInfo() ) {    // for every peer in the PeerList
            CompletableFuture.runAsync(() -> {  // run separately to avoid blocking
                try {
                    PeerConnection peerConnection = new PeerConnection();   // create a new PeerConnection
                    peerConnection.startConnection( peer.getHOST(), peer.getPORT() );   // connect to the peer
                    // retrieve the PeerInfo of the peer we are connected t o
                    PeerInfo syncedPeerInfo = peerConnection.sendJoinMessage( currentPeerInfo );
                    peerList.replacePeer( syncedPeerInfo );    // replace the peer's info in the PeerList
                    peerConnection.stopConnection();    // disconnect from the peer
                } catch (IOException | ClassNotFoundException e) {
                    if ( e instanceof ConnectException ) {
                        peerList.removePeerFromList( peer.getID() );
                    }
                }
            });
        }
    }

    /**
     * Send move to each of the peers in the net
     * @param move      the move we want to make
     * @param console   the console that will output information about the taken action
     */
    public void sendMove ( String move, OutputHandler console ) {

        // if a move has already been made in the current round do nothing
        if ( currentPeerInfo.getCurrentMove() != null) {
            return;
        }
        currentPeerInfo.setCurrentMove( move ); // else set the current move to the provided move
        if ( peerList.getPeerList().size() == 0) {    // if there are no peers to play with in the net we wait
            console.handleMsg( PreparedMessages.waitForPeersMessage() );    // send appropriate message to user
            return;
        }
        // check if all peers in the net have made their move
        // if all have then the returned message contains the round score
        String message = GameManager.checkEndGame(peerList, currentPeerInfo );
        // if the returned message is not empty then we print it as part of a score message
        if ( !message.equals( "" ) ) {
            console.handleMsg( PreparedMessages.scoreMessage( message ) );
        }

        // for every peer in the PeerList we send info about our own move to them
        for ( PeerInfo peer : peerList.getPeerInfo() ) {
            CompletableFuture.runAsync( () -> { // not blocking
                try {
                    PeerConnection peerConnection = new PeerConnection();   // create a connection
                    peerConnection.startConnection( peer.getHOST(), peer.getPORT() );   // connect to the peer
                    peerConnection.sendMoveMessage( move, currentPeerInfo );    // send our move to said peer
                    peerConnection.stopConnection();    // disconnect from the peer
                } catch (IOException | ClassNotFoundException e) {
                    if (e instanceof ConnectException) {
                        peerList.removePeerFromList( peer.getID() );  // if we're unable to connect to a peer remove it from the list
                        // check if all peers have made their moves and print the score if they have
                        String msg = GameManager.checkEndGame(peerList, currentPeerInfo );
                        if ( !msg.equals( "" ) ) {
                            console.handleMsg( PreparedMessages.scoreMessage( msg ) );
                        }
                    }
                }
            });
        }
    }

    /**
     * Enables operations on the PeerList
     */
    private class PeerTableManipulator implements ControllerObserver {

        /**
         * Add a peer to the PeerList
         * @param peer
         */
        @Override
        public void addPeer(PeerInfo peer) {
            peerList.addPeerToList(peer);
        }

        /**
         * Remove a peer from the PeerList
         * @param peer
         */
        @Override
        public void removePeer(PeerInfo peer) {
            peerList.removePeerFromList( peer.getID() );

            // check if the game round is done now that the peer has been removed
            String message = GameManager.checkEndGame(peerList, currentPeerInfo );
            if ( !message.equals( "" ) ) {
                console.handleMsg( PreparedMessages.scoreMessage( message ) );
            }
        }

        /**
         * Getter for PeerInfo of this client
         * @return  this client's PeerInfo
         */
        @Override
        public PeerInfo getPeerInfo() {
            return currentPeerInfo;
        }

        /**
         * Set the move of a peer
         * @param move  the move we want to make
         * @param peer  the Peer that makes the move
         */
        @Override
        public void setPeerMove(String move, PeerInfo peer) {
            peerList.setPeerMove( peer, move );

            // check if the game round is done now that we've set the move of the peer
            String message = GameManager.checkEndGame(peerList, currentPeerInfo );
            if ( !message.equals( "" ) ) {
                console.handleMsg( PreparedMessages.scoreMessage( message ) );
            }
        }
    }

}
