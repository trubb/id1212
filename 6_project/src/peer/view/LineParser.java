package peer.view;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Deals with parsing of messages
 */
public class LineParser {

    // matches IP addresses to ensure that they are on the four triplets of numbers between 0-255 delimited by period characters
    private final String IP_REGEX = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
    // matches network ports between 0 and 65535
    private final String PORT_REGEX = "(6553[0-5]|655[0-2][0-9]\\d|65[0-4](\\d){2}|6[0-4](\\d){3}|[1-5](\\d){4}|[1-9](\\d){0,3})";
    private ArrayList<String> arguments = new ArrayList<>();    // arraylist for keeping passed arguments
    private Command command;    // what type of command we have been passed

    /**
     * Constructor, passes the argument to the determineCommand method
     * @param rawLine   the string we want to dissect
     */
    public LineParser ( String rawLine ) {
        determineCommand( rawLine );
    }

    /**
     * Figure out what command was passed as part of the provided message
     * @param rawLine   the message we want to dissect
     */
    private void determineCommand ( String rawLine ) {
        StringTokenizer stringTokenizer = new StringTokenizer( rawLine );   // break up the message into tokens

        if ( stringTokenizer.countTokens() == 0 ) { // if there was an empty message ""
            this.command = Command.NO_COMMAND;      // then obviously there was no command posted
            return;
        }

        String cmd = stringTokenizer.nextToken().toUpperCase(); // grab the first token and UPPERCASE it
        switch (cmd) {
            case "CONNECT": // if we are to connect, check if there are more tokens
                if ( !stringTokenizer.hasMoreTokens() ) {   // if there is no 2nd token we lack an IP
                    throw new IllegalArgumentException( "missing IP address!" );
                }
                this.command = Command.CONNECT; // set commandType
                String ip = stringTokenizer.nextToken();    // set ip to be the 2nd token
                if ( !Pattern.matches(IP_REGEX, ip) ) {     // if what is provided isnt on the ip format
                    throw new IllegalArgumentException( "invalid IP address!" );
                }
                arguments.add(ip);  // add ip to argument list
                if ( !stringTokenizer.hasMoreTokens() ) {   // if there are no 3rd token we lack a port
                    throw new IllegalArgumentException( "missing port!" );
                }
                String port = stringTokenizer.nextToken();  // set port to be 3rd token
                if ( !Pattern.matches(PORT_REGEX, port) ) { // if what is provided isnt on the port range
                    throw new IllegalArgumentException( "invalid port!" );
                }
                arguments.add( port );  // add port to argument list
                break;
            case "QUIT":    // if we are to quit
                this.command = Command.QUIT;    // set commandtype to quit
                break;
            default:    // default is to make a move, if none of the options match the command it's invalid
                if ( !"ROCK".equals( cmd ) && !"PAPER".equals( cmd ) && !"SCISSORS".equals( cmd ) ) {
                    throw new IllegalArgumentException( "Invalid move '" + cmd + "'!" );
                }
                this.command = Command.MAKE_MOVE;   // we want to make a move
                arguments.add( cmd );   // add the move to arguments
        }
    }

    /**
     * Getter for argumentlist
     * @param index which of the arguments we want to grab [0-1]
     * @return      the argument at the specified index
     */
    public String getArgument ( int index ) {
        return arguments.get( index );
    }

    /**
     * Getter for the command
     * @return  the command that was specified in the input
     */
    public Command getCommand() {
        return command;
    }
}
