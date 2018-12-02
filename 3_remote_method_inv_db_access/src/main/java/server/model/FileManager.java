package server.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides some server-side file-related utilities
 */
public class FileManager {

    // TODO - CHANGE TO OTHER DIRECTOR TO SEE WHAT HAPPENS? SERVERFILES
    // the directory where files are located on disk
    private static Path filedirectory = Paths.get( "./serverfiles" );

    /**
     * get a file (file content) from a specified file
     * @param filename  the name of the file
     * @return          the content of the file
     * @throws IOException
     */
    public static byte[] getFile (String filename) throws IOException {
        // get the path to the specific file
        Path path = filedirectory.resolve( Paths.get( filename ) );
        // read from the file
        return Files.readAllBytes( path );
    }

    /**
     * TODO THIS SHIT
     * @param filename
     * @param data
     * @throws IOException
     */
    public static void persistFile ( String filename, byte[] data ) throws IOException {
        Path path = filedirectory.resolve( Paths.get( filename ) );
        Files.write(path, data);
    }

    /**
     * Deletes a file
     * @param name  the file to be deleted
     * @throws IOException
     */
    public static void deleteFile (String name) throws IOException {
        // get the path to the specific file
        Path path = filedirectory.resolve( Paths.get( name ) );
        // delete the file if it exists
        Files.deleteIfExists( path );
    }
}
