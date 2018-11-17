package client.view;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CommandLine {
    private Command command;
    private ArrayList<String> args = new ArrayList<>();

    public CommandLine ( String input ) {
        determineCommand(input);
    }

    private void determineCommand ( String input ) {
        StringTokenizer stringTokenizer = new StringTokenizer(input);

        if (stringTokenizer.countTokens() == 0) {
            this.command = Command.NO_COMMAND;
            return;
        }

        String cmd = stringTokenizer.nextToken().toUpperCase();

        switch (cmd) {
            case "PLAY":
                this.command = Command.PLAY;
                break;
            case "QUIT":
                this.command = Command.QUIT;
                break;
            default:
                this.command = Command.SEND;
                args.add(cmd);
        }
    }

    public String getArgument ( int index ) {
        return args.get( index );
    }

    public Command getCommand() {
        return command;
    }

}
