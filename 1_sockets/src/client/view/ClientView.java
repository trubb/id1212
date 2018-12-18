package client.view;

/**
 * NEW: View-side printing of messages from the server
 */
public class ClientView implements ConnectorListener {

    @Override
    public void receive(String message) {
        System.out.println( message );
    }
}
