package client.view;

/**
 * All available commands
 */
public enum Command {
    // register a user
    REGISTER,
    // remove a user
    UNREGISTER,
    // log into the catalogue with an existing user
    LOGIN,
    // log out from the existing user
    LOGOUT,
    // store a file in the catalogue
    STORE,
    // retrieve a file from the catalogue
    GET,
    // list files that are in the catalogues
    LIST,
    // remove a file in the catalogue
    DELETE,
    // update (overwrite) an existing file
    UPDATE,
    // print commands available to users
    HELP,
    // quit the program
    QUIT,
    // invalid input given
    NO_COMMAND
}