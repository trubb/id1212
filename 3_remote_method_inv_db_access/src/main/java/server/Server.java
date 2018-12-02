package server;

import server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            // New server instance
            Server server = new Server();
            // Start running the actual server implementation
            server.startRMIServant();
            System.out.println("Server running");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the RMI servant
     * Start the controller
     * @throws RemoteException          RMI likes its RemoteExceptions
     * @throws MalformedURLException    If the URL can't be parsed properly
     */
    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();    // locate the registry if it exists
        } catch (RemoteException noRegistryRunning) {
            // if the registry doesnt exist then create a registry
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        // initialize the controller
        Controller controller = new Controller();
        // Rebinds the name "catalog" to the controller
        Naming.rebind("catalog", controller);
    }

}

/*
DONE - owner defines who has full access, if read access then a user can list the file, if write then a user can overwrite
DONE - notify users to deletion
DONE - notify users to updates
DONE - functional update
DONE - user can also define if private, mer verklighetstroget att ha den restriktionen med
DONE - can register login/logout, actions requires login
DONE - registering requires unique username, and a password
DONE - logging in requires username and password, verified by server
DONE - user can "upload" a local file to the server, and "download" them
DONE - a file can be accessed by any user that is registered
DONE - all users can list all files and their attributes (metadata)
DONE - the owner is notified when other users access their files - OPT-IN only
 */
