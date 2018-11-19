package client.view;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class InputReader {
    private ArrayList<String> arguments = new ArrayList<>();
    private Commands commands;

    public InputReader (String rawInput) {
        determineCommand(rawInput);
    }

    private void determineCommand (String rawInput) {
        StringTokenizer stringTokenizer = new StringTokenizer(rawInput);

        if (stringTokenizer.countTokens() == 0) {
            this.commands = Commands.NO_COMMAND;
            return;
        }

        String command = stringTokenizer.nextToken().toUpperCase();
        switch (command) {
            case "CONNECT":
              this.commands = Commands.CONNECT;
                break;
            case "QUIT":
                this.commands = Commands.QUIT;
                break;
            case "START":
                this.commands = Commands.START;
                break;
            default:
                this.commands = Commands.GUESS;
                arguments.add(command);
        }
    }


    public String getArgument (int index) {
        return arguments.get(index);
    }

    public Commands getCommands() {
        return commands;
    }

}
