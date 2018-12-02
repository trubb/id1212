package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides some file-related utilities and definitions
 */
public class FileUtil {

    // TODO - USERFILES
    // the directory where our files are located on disk
    private static Path filedirectory = Paths.get( "./files" );

    /**
     * Write a file from the db
     * @param filename  the name of the file
     * @param bytes     the content of the file
     * @throws IOException
     */
    public static void writeFile ( String filename, byte[] bytes ) throws IOException {
        // get the path to the specific file
        Path filepath = filedirectory.resolve( Paths.get( filename ) );
        // write to the file path
        Files.write( filepath, bytes );

    }

    /**
     * Read a file from the disc
     * @param filename  the file to be read
     * @return          the content of the file
     * @throws IOException
     */
    public static byte[] readFile (String filename) throws IOException {
        // get the path to the specified file
        Path filepath = filedirectory.resolve( Paths.get( filename ) );
        // if the file doesnt exist then say so
        if ( !Files.exists( filepath ) ) {
            throw new FileNotFoundException("couldnt find " + filepath);
        }
        // return the content of the file
        return Files.readAllBytes( filepath );

    }

}
