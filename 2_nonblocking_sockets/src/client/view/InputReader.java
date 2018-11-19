package client.view;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class InputReader {
    private ArrayList<String> arguments = new ArrayList<>();    // for storing passed commands
    private Commands commands;  // allowed and recognized commands

    /**
     * Take in a String input to determine what it is
     * @param input the string we want to check
     */
    public InputReader (String input) {
        determineCommand(input);
    }

    /**
     * Get the argument at the specified index
     * @param index the index we're interested in
     * @return the argument
     */
    public String getArgument (int index) {
        return arguments.get(index);
    }

    /**
     * Returns the commmand
     * @return the command
     */
    public Commands getCommands() {
        return commands;
    }

    /**
     * Determine what sort of command the input is
     * @param input the string we want to check
     */
    private void determineCommand (String input) {
        StringTokenizer stringTokenizer = new StringTokenizer(input);   // tokenize the string

        if (stringTokenizer.countTokens() == 0) {   // if there are no tokens in the provided string (empty)
            this.commands = Commands.NO_COMMAND;    // then it wasnt a command
            return; // so we exit
        }

        String command = stringTokenizer.nextToken().toUpperCase(); // grab the token and check what command it is
        switch (command) {
            case "QUIT":
                this.commands = Commands.QUIT;
                break;
            case "START":
                this.commands = Commands.START;
                break;
            default:
                this.commands = Commands.GUESS;
                arguments.add(command); // if it is a guess we add the command to the arguments for later use
        }
    }

}
