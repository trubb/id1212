package shared;

public enum MessageType {

    START,  // start a new game
    GUESS,  // make a guess
    QUIT,   // quit playing
    START_RESPONSE, // server reply on new game
    GUESS_RESPONSE, // server reply after guess
    END_RESPONSE    // server response on game end

}
