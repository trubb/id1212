package common;

public class Message {

    private String message;
    private int attemps;
    private String guessArray;
    private int score;
    private String guessedChars;

    public Message(String message, int attemps, String guessArray, int score, String guessedChars) {
        this.message = message;
        this.attemps = attemps;
        this.guessArray = guessArray;
        this.score = score;
        this.guessedChars = guessedChars;
    }

    /**
     * Serialize the message to enable sending through a printWriter
     * @return  a String containing the Message object's information
     */
    public String serialize () {
        return message + "##" +
                attemps + "##" +
                guessArray + "##" +
                score + "##" +
                guessedChars;
    }

    /**
     * When recieved, split a String into a new Message
     * @param message   The string to be deserialized
     * @return          a Message object containing the information
     */
    public static Message deserialize (String message) {
        String[] messageParts = message.split("##");
        return new Message( messageParts[0],
                Integer.parseInt(messageParts[1]),
                messageParts[2],
                Integer.parseInt(messageParts[3]),
                messageParts[4] );
    }

    /**
     * Provides the contents of  the Message object in a nicely formatted String
     * @return  The content of the object, formatted
     */
    public String printableMessage() {
        return message +
            "Attempts remaining: " + attemps +
            "\n" + guessArray +
            "\nScore: " + score +
            "\n" + guessedChars;
    }

}
