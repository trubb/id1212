package common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Catalog extends Remote {

    void register (String username, String password) throws RemoteException;

    void unregister(String username, String password) throws RemoteException;

    UserDTO login(String username, String password) throws RemoteException;

    List<? extends FileDTO> listAllFiles(UserDTO owner) throws RemoteException;

    List<? extends FileDTO> listAllFiles() throws RemoteException;

    void storeFile(UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead) throws IOException;

//    byte[] getFile(UserDTO user, String name) throws IOException;
    // not implemented, instead only metadata from the database is passed

    void updateFile(UserDTO owner, String name, byte[] content, boolean privateAccess, boolean publicWrite, boolean publicRead) throws IOException;

    void deleteFile(UserDTO user, String name) throws IOException;

    void notify(UserDTO user, String name, ClientRemote outputHandler) throws RemoteException;
}
