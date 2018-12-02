package client.view;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A class for extracting commands and arguments from user input
 */
public class LineParser {

    // the arraylist that will hold our passed arguments
    private ArrayList<String> arguments = new ArrayList<>();
    // the available commands
    private Command command;

    /**
     * Constructor, parses an input string
     * @param input the string we want to dissect to figure out what to do
     */
    public LineParser (String input) {
        findCommand( input );
    }

    /**
     * Getter for the arguments list
     * @param index the item we want to get from the list of arguments
     * @return      the specified item
     */
    public String getArg (int index) {
        return arguments.get( index );
    }

    /**
     * Getter for the matched command that has been retrieved from the input
     * @return  the command that was inferred from the input
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Find what command matches the provided input and extract the required arguments
     * @param input the string that we want to dissect to find out what to do
     */
    private void findCommand (String input) {
        // stringtokenizer containing the input string, broken up into:
        // [command] [param0] [param1] [param2] [param3] etc.
        StringTokenizer stringTokenizer = new StringTokenizer( input );
        // if we've been passed an empty string then obviously no command was passed, return
        if ( stringTokenizer.countTokens() == 0 ) {
            this.command = Command.NO_COMMAND;
            return;
        }

        String cmd = stringTokenizer.nextToken().toUpperCase(); // set the first token ([command]) to uppercase for comparison
        switch ( cmd ) {
            // a new user wants to register
            case "REGISTER":
                this.command = Command.REGISTER;    // set command type to REGISTER
                if ( !stringTokenizer.hasMoreTokens() ) {   // check if input lacks a chosen username
                    throw new IllegalArgumentException("provide a username"); // if it does then get mad
                }
                arguments.add( stringTokenizer.nextToken() );   // if there was a username provided, add to argument
                if ( !stringTokenizer.hasMoreTokens() ) {   // check if input has a chosen password
                    throw new IllegalArgumentException("choose a password");    // if it doesnt then get mad
                }
                arguments.add( stringTokenizer.nextToken() ); // if it does then add to argument
                break;
            case "UNREGISTER":  // an existing user wants to remove themselves
                this.command = Command.UNREGISTER;  // set command type to UNREGISTER
                if ( !stringTokenizer.hasMoreTokens() ) {   // check if input lacks a username to remove
                    throw new IllegalArgumentException("provide a username");
                }
                arguments.add( stringTokenizer.nextToken() ); // if it has one then add to args
                if ( !stringTokenizer.hasMoreTokens() ) {   // check if input has a password to authorize the user
                    throw new IllegalArgumentException("provide a password");
                }
                arguments.add( stringTokenizer.nextToken() ); // if it does then add to args
                break;
            case "LOGIN":   // an existing user wants to log in
                this.command = Command.LOGIN;   // set command type to LOGIN
                if ( !stringTokenizer.hasMoreTokens() ) {   // if no username provided get mad
                    throw new IllegalArgumentException("Missing username");
                }
                arguments.add( stringTokenizer.nextToken() );   // if username provided then set as arg
                if ( !stringTokenizer.hasMoreTokens() ) {   // if no password provided get mad
                    throw new IllegalArgumentException("Missing password");
                }
                arguments.add( stringTokenizer.nextToken() ); // if password provided then set as arg
                break;
            case "LOGOUT":  // an logged in user wants to log out
                this.command = Command.LOGOUT;  // set command type to LOGOUT
                break;
            case "UPLOAD":  // an logged in user wants to store a file in the catalog
                this.command = Command.STORE;  // command type is STORE
                if ( !stringTokenizer.hasMoreTokens() ) { // if no file name is provided:
                    throw new IllegalArgumentException("Missing name");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a name is provided we add to args
                if ( !stringTokenizer.hasMoreTokens() ) { // if no private access is defined then we abort and say so
                    throw new IllegalArgumentException("private");
                }
                arguments.add( stringTokenizer.nextToken() ); // if private access arg is provided add to args
                if ( !stringTokenizer.hasMoreTokens() ) { // if public write access is undefined we abort and say so
                    throw new IllegalArgumentException("public write");
                }
                arguments.add( stringTokenizer.nextToken() ); // if public write access arg is provided add to args
                if ( !stringTokenizer.hasMoreTokens() ) { // if public read access is undefined we abort and say so
                    throw new IllegalArgumentException("public read");
                }
                arguments.add( stringTokenizer.nextToken() ); // if public read access arg is provided add to args
                break;
            case "DOWNLOAD":    // a logged in user wants to get a file from the catalog
                this.command = Command.GET;    // command type is GET
                if ( !stringTokenizer.hasMoreTokens() ) { // if no file name is provided then abort and say so
                    throw new IllegalArgumentException("file name");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a file name is provided then add to args
                break;
            case "UPDATE":  // a logged in user wants to update a file in the catalog
                this.command = Command.UPDATE; // command type is update
                if ( !stringTokenizer.hasMoreTokens() ) { // if no file name is provided, abort and say so
                    throw new IllegalArgumentException("name");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a file name is provided then add to args
                if ( !stringTokenizer.hasMoreTokens() ) { // if no private access arg is provided, abort and say so
                    throw new IllegalArgumentException("private");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a private access arg is provided then add to args
                if ( !stringTokenizer.hasMoreTokens() ) { // if no public write arg is provided, abort and say so
                    throw new IllegalArgumentException("public write");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a public write arg is provided then add to args
                if ( !stringTokenizer.hasMoreTokens() ) { // if no public read arg is provided, abort and say so
                    throw new IllegalArgumentException("public read");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a public read arg is provided then add to args
                break;
            case "DELETE":  // a logged in user wants to remove a file in the catalog
                this.command = Command.DELETE; // command is DELETE
                if ( !stringTokenizer.hasMoreTokens() ) { // if no file name is provided, abort and say so
                    throw new IllegalArgumentException("file name");
                }
                arguments.add( stringTokenizer.nextToken() ); // if a file name is provided then add to args
                break;
            case "LIST":  // a logged in user wants to list files in the catalog
                this.command = Command.LIST; // command is LIST
                break;
            case "QUIT":  // a user wants to quit the program
                this.command = Command.QUIT;    // command is quit
                break;
            default:    // if no command is recognized then the user will be reminded of all commands and how to use them
                this.command = Command.HELP;
        }
    }
}
