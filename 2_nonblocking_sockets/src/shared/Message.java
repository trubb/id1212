package shared;

public class Message {

    private MessageType messageType;    // type of message
    private String messageText; // message text

    /**
     * Create a message by calling this
     * @param type the type
     * @param message the message text
     */
    public Message ( MessageType type, String message ) {
        this.messageType = type;
        this.messageText = message;
    }

    /**
     * hand back the message type
     * @return message type of a messager object
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * hand back the message text
     * @return message text of a message object
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * Serialise a message by putting together message type and text but separated with ##
     * @param message the Message we want to serialize
     * @return said message, but serialized
     */
    public static String serializeMessage (Message message) {
        return message.messageType.toString() + "##" + message.messageText;
    }

    /**
     * Deserialise a message by ripping it apart at the ##
     * @param message the message we want to deserialise
     * @return said message, but deserialised
     */
    public static Message deserializeMessage (String message) {
        String[] messageParts = message.split("##");    // split on ## and put into a String-array
        MessageType type = MessageType.valueOf( messageParts[0].toUpperCase() );    // grab the type
        String body;
        if (messageParts.length > 1) {  // grab the message text
            body = messageParts[1];
        } else {
            body = "";
        }
        return new Message( type, body );   // return the message, but separated into parts
    }

}
