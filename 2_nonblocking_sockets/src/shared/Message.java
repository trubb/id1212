package shared;

public class Message {

    private MessageType messageType;
    private String message;

    public Message (MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public static String serialize (Message message) {
        return message.messageType.toString() + "##" + message.message;
    }

    public static Message deserialize (String message) {
        String[] messageParts = message.split("##");
        MessageType type = MessageType.valueOf( messageParts[0].toUpperCase() );
        //String body = messageParts.length > 1 ? messageParts[1] : "";
        String body;
        if (messageParts.length > 1) {
            body = messageParts[1];
        } else {
            body = "";
        }
        return new Message(type, body);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() { // getBody
        return message;
    }

}
