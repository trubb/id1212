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
