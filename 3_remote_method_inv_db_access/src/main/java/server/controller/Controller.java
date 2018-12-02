package server.controller;

import common.Catalog;
import common.ClientRemote;
import common.FileDTO;
import common.UserDTO;
import server.integration.FileDAO;
import server.integration.UserDAO;
import server.model.File;
import server.model.User;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

public class Controller extends UnicastRemoteObject implements Catalog {

    // the user data access object
    private final UserDAO userDAO;
    // the file data access object
    private final FileDAO fileDAO;
    // Users that can be notified
    private HashMap<Integer, ClientRemote> notifiableUsers = new HashMap();

    // controller constructor
    public Controller() throws RemoteException {
        // call server constructor
        super();
        // initialize DAOs
        this.userDAO = new UserDAO();
        this.fileDAO = new FileDAO();
    }

    /**
     * Register a user: create a username and a password for the user
     * @param username          The chosen username
     * @param password          The chosen password
     * @throws RemoteException
     */
    @Override
    public void register ( String username, String password ) throws RemoteException {
        // As long as there is no other user with the same name
        // Store the user with the provided username and password
        if (userDAO.findUser(username) == null) {
            userDAO.storeUser(new User(username, password));
        } else {
            throw new RemoteException( username + " is taken");
        }
    }

    /**
     * Removes a registered user from the db
     * @param username  the user to be removed
     * @param password  the associated password
     */
    @Override
    public void unregister ( String username, String password ) {
        // Look for the user in the db
        User user = userDAO.findUser(username);
        // if the user exists and the password matches we remove the user
        if ( user != null && ( password.equals( user.getPassword() ) ) ) {
            userDAO.removeUser(user);
        }
    }

    /**
     * Log in a user that exists in the database
     * @param username  the user's username
     * @param password  the user's password
     * @return          a user object that is this user
     * @throws RemoteException
     */
    @Override
    public UserDTO login ( String username, String password ) throws RemoteException {
        // Look for user in the db
        User user = userDAO.findUser( username );
        // if the user exists and the passwords matches we return the user, logged in
        if ( user != null && ( password.equals( user.getPassword() ) ) ) {
            return user;
        } else {
            // if the password doesnt match the stored password we say NO!
            throw new RemoteException("password or username wrong");
        }
    }

    /**
     * List all files that are stored, disregard owner
     * @return                  a list of the files
     * @throws RemoteException
     */
    @Override
    public List<? extends FileDTO> listAllFiles() throws RemoteException {
        return listAllFiles( null );
    }

    /**
     * Find all files belonging to a specified owner
     * @param owner the owner whose files we want to list
     * @return      a list of files belonging to the owner
     */
    @Override
    public List<? extends FileDTO> listAllFiles (UserDTO owner) {
        User user;
        if ( owner == null ) {    // if we do not have a owner provided
            user = null;        // pass null user (list all)
        } else {
            // get the user's username and assign to user
            user = userDAO.findUser( owner.getUsername() );
        }
        // list of all files belonging to the specified user
        return fileDAO.listFiles(user);
    }

    /**
     * Store a "file" in the database
     * @param owner             owner of the file (current user)
     * @param name              name of the file
     * @param content           file content as a representation of its size
     * @param privateAccess     is the file accessible by others
     * @param publicWrite       can any user overwrite it
     * @param publicRead        can any user read it
     * @throws IOException
     */
    @Override
    public void storeFile ( UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead ) throws IOException {
        // if a file with that name does not already exist
        if ( fileDAO.findFile(name) == null ) {
            // then "create" a "file" in the db with the provided parameters
            fileDAO.storeFile( new File( userDAO.findUser( owner.getUsername() ), name, privateAccess, publicWrite, publicRead, content.length) );
        } else {
            throw new RemoteException("The file '" + name + "' already exists!");
        }
    }

    /**
     * "Updates" a file (overwrite an existing with new data)
     * @param owner             file owner
     * @param name              file name
     * @param content           content as a representation of size
     * @param privateAccess     is the file accessible by others
     * @param publicWrite       can anyone overwrite
     * @param publicRead        can anyone read
     * @throws IOException
     */
    @Override
    public void updateFile ( UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead ) throws IOException {
        // find the file owner
        User user = userDAO.findUser( owner.getUsername() );
        // find the file
        File file = fileDAO.findFile( name );
        if ( file == null ) { // if file doesnt exist
            throw new RemoteException("The file '" + name + "' does not exists!");
        } else if ( file.getOwner().getId() == user.getId() || ( !file.hasPrivateAccess() && file.hasWritePermission() ) ) {
            // if the file belongs to the provided user, or if it is accessible by any user and may be read
            // update the file with the new parameters
            fileDAO.updateFile( new File( userDAO.findUser( owner.getUsername() ), name, privateAccess, publicWrite, publicRead, content.length ) );
            // if there are users who want to be notified about this file then notify
            if ( notifiableUsers.containsKey( file.getId() ) ) {
                notifyUser( file.getId(), file.getName(), "updated" );
            }
        } else {
            throw new RemoteException("not allowed to update this file");
        }
    }

    /**
     * Delete a "file" from the db
     * @param userDTO   user that wants to perform the action
     * @param name      filename we want to operate on
     * @throws IOException
     */
    @Override
    public void deleteFile ( UserDTO userDTO, String name ) throws IOException {
        // find the user in the db
        User user = userDAO.findUser( userDTO.getUsername() );
        // find the file in the db
        File file = fileDAO.findFile( name );
        if (file == null) { // if file doesnt exist in the db then get real mad
            throw new RemoteException("file " + name + " doesnt exist");
        } else if ( file.getOwner().getId() == user.getId() || ( !file.hasPrivateAccess() && file.hasWritePermission() ) ) {
            // if the file belongs to the provided user, or if it is accessible and writeable by any user
            // then delete the file
            fileDAO.removeFile( file );
            // if there are users that want to be notified then notify them
            if ( notifiableUsers.containsKey( file.getId() ) ) {
                notifyUser( file.getId(), file.getName(), "deleted" );
            }
        } else {
            throw new RemoteException("not allowed to delete that file");
        }
    }

    /**
     * Notify users about changes to files
     * @param userDTO           A user that wants to be notified
     * @param fileName              file that we want to keep track of
     * @param outputHandler     an endpoint where we can get in touch with the client
     * @throws RemoteException
     */
    @Override
    public void notify( UserDTO userDTO, String fileName, ClientRemote outputHandler ) throws RemoteException {
        System.out.println("Works?");
        // find the user in the db
        User user = userDAO.findUser( userDTO.getUsername() );
        // find the file in the db
        File file = fileDAO.findFile( fileName );
        if ( file == null ) { // if the file doesnt exist get real mad
            throw new RemoteException("file " + fileName + " doesnt exist");
        } else if ( file.getOwner().getId() == user.getId() ) {
            // if the file's owner is the provided user then add to the list of users/files that want to be notified
            notifiableUsers.put( file.getId(), outputHandler );
        } else {
            // else get real mad again
            throw new RemoteException("not allowed to be notified about that file");
        }
    }

    /**
     * Notify a user that is in the nofify list
     * @param fileId    the id of the file we notify for
     * @param fileName  the filename that we notify for
     * @param action    the action that has been performed on the file
     * @throws RemoteException
     */
    private void notifyUser ( int fileId, String fileName, String action ) throws RemoteException {
        // get the corresponding output handler for the user so we can get in touch
        ClientRemote outputHandler = notifiableUsers.get( fileId );
        // send a message about what has happened to the file
        outputHandler.message( fileName + " was " + action );
    }
}
