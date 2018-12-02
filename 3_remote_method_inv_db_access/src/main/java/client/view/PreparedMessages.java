package client.view;

/**
 * Messages that occur in many places and therefore benefit from being predefined
 */
public class PreparedMessages {

    /**
     * A message for displaying errors
     * @param reason    why are we telling the user they did wrong?
     * @return          How can they do better next time?
     */
    public static String errorMsg (String reason) {
        return "faulty command format " + reason + "\n";
    }

    /**
     * List of commands that are available and what they do
     * @return  the list of commands
     */
    public static String commands() {
        return "the following commands are available: \n" +
               "register <username> <password> - registers a new user\n" +
               "unregister <username> <password> - removes an existing user\n" +
               "login <username> <password> - logs in an existing user\n" +
               "logout - logs out from the current session\n" +
               "upload <filename> <private(true/false)> <write(true/false)> <read(true/false)> - uploads a file with permissions\n" +
               "download <filename> - retrieve a file from the database\n" +
               "update <filename> <private(true/false)> <write(true/false)> <read(true/false)> - updates permissions of a file\n" +
               "list <username (OPTIONAL)> - lists all files, optionally lists all files that are accessible by a user\n";
    }
}