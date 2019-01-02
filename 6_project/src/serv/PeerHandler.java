package serv;

import common.Message;
import common.MessageWrapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

public class PeerHandler {

    private static final Logger LOGGER = Logger.getLogger( PeerHandler.class.getName() );
    private Socket clientSocket;
    private StartServ server;

    public PeerHandler(Socket clientSocket, StartServ server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void run() {
        try {

            ObjectOutputStream out = new ObjectOutputStream( clientSocket.getOutputStream() );
            ObjectInputStream in = new ObjectInputStream( clientSocket.getInputStream() );

            MessageWrapper message = (MessageWrapper) in.readObject();

            // join -> add to table
            // leave -> remove from table   TODO - remove these comments

            if ( message.getMessage().equals(Message.JOIN) ) {
                LOGGER.info( "Peer at "+ message.getSenderPeerInfo().getPORT() + " joined network" );
                server.peerTable.addPeerToTable( message.getSenderPeerInfo() );
                out .writeObject( server.peerTable );
                System.out.println( server.peerTable );
            } else if ( message.getMessage().equals( Message.LEAVE ) ) {
                LOGGER.info( "Peer at " + message.getSenderPeerInfo().getPORT() + " left network" );
                server.peerTable.removePeerFromTable( message.getSenderPeerInfo().getID() );
                System.out.println( server.peerTable );
            }

            // TODO REMOVE THIS COMMENT
            // close streams & socket
            in.close();
            out.close();
            clientSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
}
