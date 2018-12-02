package client;

import client.view.Controller;
import common.Catalog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    /**
     * Start a connection to the server by finding the catalogue
     * @param args not used
     */
    public static void main(String[] args) {
        try {
            // find the catalog by name, which the server has set when starting
            Catalog catalog = (Catalog) Naming.lookup("catalog");
            // start the controller which in turn will handle everything for us
            new Controller().start(catalog);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            // if something goes wrong with connecting we abort
            System.out.println("Could not start catalog shell!");
        }
    }
}