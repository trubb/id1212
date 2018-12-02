package client.view;

import client.FileUtil;
import common.Catalog;
import common.FileDTO;
import common.UserDTO;
import common.ClientRemote;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

/**
 * A class for performing actions on the catalog
 */
public class CatalogShell implements Runnable {

    // catalog object for dealing with the server
    private Catalog catalog;
    // we arent running yet
    private boolean running = false;
    // user data transfer object, init to null
    private UserDTO user = null;
    // "please write here" character
    private static final String PROMPT = ">";
    // output manager for the client
    private final ThreadSafeStdOut outputManager = new ThreadSafeStdOut();
    // input source
    private final Scanner console = new Scanner( System.in );
    // for dealing with notifications when someone accesses our stuff
    private NotifyEndpoint notifyOutput;

    /**
     * Start the clientside shell
     * @param catalog   the catalog we want to connect to
     */
    public void start(Catalog catalog) {
        // point to the correct catalog instance
        this.catalog = catalog;

        // initialize the outputhandler
        try {
            this.notifyOutput = new NotifyEndpoint();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // if already running then exit here
        if (running) {
            return;
        }
        // else we're not started yet so set running to true
        running = true;

        // and run the run() method so this thread can get going for real
        new Thread(this).start();
    }

    /**
     * Run this thread properly
     */
    @Override
    public void run() {
        outputManager.println( "file catalog started 'help' lists commands quit with 'quit'\n" );
        // print the prompt char
        outputManager.print( PROMPT );

        // while we're running
        while ( running ) {
            try {
                // grab the user input
                LineParser parsedLine = new LineParser( console.nextLine() );
                // extract the command from this input
                Command command = parsedLine.getCommand();

                // make sure that the user is logged in, if not tell them to log in
                if (
                        user == null &&                             // if the user isnt logged in
                        !command.equals( Command.REGISTER ) &&      // and if the command isnt register
                        !command.equals( Command.UNREGISTER ) &&    // and if the command isnt unregister
                        !command.equals( Command.LOGIN ) &&         // and if the command isnt login
                        !command.equals( Command.QUIT ) &&          // and if the command isnt quit
                        !command.equals( Command.HELP ) &&          // and if the command isnt help
                        !command.equals( Command.NO_COMMAND )       // and if the command isnt illegal
                ) {
                    // then they are trying to do stuff without being logged in, tell them off
                    outputManager.print( "you have to log in" );
                    outputManager.print( PROMPT ); // print the prompt char
                    continue;   // and exit this loop
                }

                // do things based on what command we've been passed
                switch ( command ) {
                    case QUIT:  // quit
                        running = false;
                        break;
                    case NO_COMMAND:    // if no command is provided then exit current loop so we can take another try
                        break;
                    case HELP:  // print available commands
                        outputManager.print( PreparedMessages.commands() );
                        break;
                    case REGISTER:  // if the command is "register" then we add the user to the db
                        // register the user in the catalog and tell them we did so
                        catalog.register( parsedLine.getArg(0), parsedLine.getArg(1) );
                        outputManager.println( "registered " + parsedLine.getArg(0) );
                        break;
                    case UNREGISTER:
                        // remove the user from the catalog and tell them we did so
                        catalog.unregister( parsedLine.getArg(0), parsedLine.getArg(1) );
                        this.user = null;   // clear user object
                        outputManager.println( "unregistered " + parsedLine.getArg(0) );
                        break;
                    case LOGIN:
                        // login user that is present in the catalog and let them know
                        this.user = catalog.login( parsedLine.getArg(0), parsedLine.getArg(1) );
                        outputManager.println( "logged in " + user.getUsername() );
                        break;
                    case LOGOUT:
                        // logout user by setting the user object to null and tell them about it
                        this.user = null;
                        outputManager.println( "logged out");
                        break;
                    case STORE:
                        // store a file in the catalog
                        if ( this.user != null ) {    // if the user is logged in
                            // grab the data from the file
                            byte[] data = FileUtil.readFile( parsedLine.getArg(0) );
                            // store the file in the catalog
                            catalog.storeFile(
                                    this.user,                                          // file owner
                                    parsedLine.getArg(0),                          // file name
                                    data,                                               // file "size"
                                    Boolean.parseBoolean( parsedLine.getArg(1) ),  // private
                                    Boolean.parseBoolean( parsedLine.getArg(2) ),  // overwriteable
                                    Boolean.parseBoolean( parsedLine.getArg(3) )   // readable
                            );
                            outputManager.println( "uploaded file" );
                        } else {
                            outputManager.println( "gotta log in to upload" );
                        }
                        break;
                    case GET:
                        if ( this.user != null ) {  // if the user is logged in we try to get the file
                            // write the file to the clients local storage
                            // TODO - ONLY GET THE METADATA like name or size
                            FileUtil.writeFile( parsedLine.getArg(0), catalog.getFile( user, parsedLine.getArg(0) ) );
                            outputManager.println( "file downloaded" );
                        } else {
                            outputManager.println( "gotta log in to download files" );
                        }
                        break;
                    case LIST:
                        // list files
                        List<? extends FileDTO> list;
                        if ( user != null ) {   // if a user is provided then we check for that user
                            list = catalog.listAllFiles( user );
                        } else {    // else we find all files
                            list = catalog.listAllFiles();
                        }
                        outputManager.println( "filename (size [Bytes]) - private / write / read" );
                        for ( FileDTO file : list ) { // for every file in the list display name, size, permissions
                            outputManager.println(
                                    file.getName() +                            // file name
                                    " ( " + file.getDimension() + " B ) - " +   // file "size"
                                    file.hasPrivateAccess() + " / " +           // file access
                                    file.hasWritePermission() + " / " +         // write permission
                                    file.hasReadPermission()                    // read permission
                                );
                        }
                        break;
                    case DELETE:
                        if ( this.user != null ) {    // if the user is logged in we try to remove the file
                            // remove the file from the catalog and tell the client about it
                            catalog.deleteFile( user, parsedLine.getArg(0) );
                            outputManager.println( "file deleted" );
                        } else {
                            outputManager.println( "gotta log in to delete files" );
                        }
                        break;
                    case UPDATE:
                        if ( this.user != null ) {    // if the user is logged in we try to update the file
                            byte[] data = FileUtil.readFile( parsedLine.getArg(0) );  // take in the file
                            catalog.updateFile(
                                    this.user,                                          // file owner
                                    parsedLine.getArg(0),                           // file name
                                    data,                                               // file "size"
                                    Boolean.parseBoolean( parsedLine.getArg(1) ),   // file access
                                    Boolean.parseBoolean( parsedLine.getArg(2) ),   // write permission
                                    Boolean.parseBoolean( parsedLine.getArg(3) )    // read permission
                            );
                            outputManager.println( "updated file" );
                        } else {
                            outputManager.println( "gotta log in to delete files" );
                        }
                        break;
                    case NOTIFY:
                        if ( this.user != null ) {    // if the user is logged in we try to notify
                            catalog.notify( this.user, parsedLine.getArg(0), this.notifyOutput);
                        } else {
                            outputManager.println( "gotta log in to be notified" );
                        }
                        break;
                }
                outputManager.print( PROMPT );
            } catch (IllegalArgumentException | IOException e) {    // if something goes wrong we get real mad
                e.printStackTrace();
                outputManager.print( PreparedMessages.errorMsg( e.getMessage() ) );
                outputManager.print( PROMPT );
            }
        }
    }

    /**
     * Clientside endpoint for notifications
     */
    private class NotifyEndpoint extends UnicastRemoteObject implements ClientRemote {
        // empty constructor
        public NotifyEndpoint() throws RemoteException {
        }

        /**
         * Print a notification
         * @param notification  the notification to be printed
         * @throws RemoteException
         */
        @Override
        public void message (String notification) throws RemoteException {
            outputManager.println( notification );
            outputManager.print( PROMPT );
        }
    }

}