package client.view;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class InputReader {
    private final String IP_REGEX = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
    private final String PORT_REGEX = "(6553[0-5]|655[0-2][0-9]\\d|65[0-4](\\d){2}|6[0-4](\\d){3}|[1-5](\\d){4}|[1-9](\\d){0,3})";
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
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new IllegalArgumentException("Missing IP Address");
                }
                this.commands = Commands.CONNECT;
                String ipaddr = stringTokenizer.nextToken();
                if ( !Pattern.matches( IP_REGEX, ipaddr ) ) {
                    throw new IllegalArgumentException("Invalid IP Address");
                }
                arguments.add(ipaddr);
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new IllegalArgumentException("Missing port");
                }
                String port = stringTokenizer.nextToken();
                if ( !Pattern.matches( PORT_REGEX, port ) ) {
                    throw new IllegalArgumentException("Invalid port!");
                }
                arguments.add(port);
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
