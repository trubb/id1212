package client.view;

import common.Message;

/**
 * NEW: View-side printing of messages from the server
 */
public class ClientView implements ConnectorListener {

    @Override
    public void receive(String message) {

        if ( message.contains("#") ) {
            Message msg = Message.deserialize( message );
            System.out.println( msg.printableMessage() );
        } else {
            System.out.println( message );

        }
    }
}
