package server.integration;

import server.model.File;
import server.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAO {

    // entitymanagerfactory for the class
    private final EntityManagerFactory entityManagerFactory;
    // To provide thread local entitymanagers we create an object for doing so
    private final ThreadLocal<EntityManager> managerThreadLocal = new ThreadLocal<>();

    // fileDAO constructor, create the entitymanagerfactory for this persistence unit
    public FileDAO() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.netprog.jpa");
    }

    /**
     * List all files owned by the provided user
     * @param owner the owner we want to list files for
     * @return      a list of files owned by this user
     */
    public List<File> listFiles (User owner) {
        // create a new entitymanager
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // create a new query via the entitymanager, selects files from all files that are
        // publicly accessible or where the provided user is the owner
        Query query = entityManager.createQuery("SELECT f FROM File f WHERE (f.privateAccess=false OR f.owner=:owner)");
        // set the owner parameter of the query to be the provided owner
        query.setParameter("owner", owner);
        // create a list to hold the files
        List<File> files;

        // populate the list with the result from the query
        // if there are no files returned then we will return an empty arraylist
        try {
            files = query.getResultList();
        } catch (NoResultException e) {
            files = new ArrayList<>();
        }

        // close the entitymanager when we're done with it and return our result
        entityManager.close();
        return files;
    }

    /**
     * find a specific file by name
     * @param name  name of the file we want to find
     * @return      the file, if it exists in the db
     */
    public File findFile (String name) {
        // create an entitymanager
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // create a new query via the entitymanager, select file matching the provided name from the db
        Query query = entityManager.createQuery("SELECT f FROM File f WHERE f.name=:name");
        // set the name parameter to be the provided filename
        query.setParameter("name", name);
        // file object to hold our file
        File file;

        // as File.name are unique we get one result, put it in the file object, if no result then return null
        try {
            file = (File) query.getSingleResult();
        } catch (NoResultException e) {
            file = null;
        }

        // close the entitymanager and return the file
        entityManager.close();
        return file;
    }

    /**
     * Store a file in the db
     * @param file  the file we want to store
     */
    public void storeFile (File file) {
        // use the helper to prep the entitymanager for putting the file in the db
        EntityManager entityManager = beginTransaction();
        // make the file object managed and persistent
        entityManager.persist(file);
        // commit the transaction via helper method
        commitTransaction();
    }

    /**
     * Update a file on the server
     * @param file  the file we want to update, passed as a file object from the caller
     */
    public void updateFile (File file) {
        // use the helper to prep the entitymanager for putting the file in the db
        EntityManager entityManager = beginTransaction();
        // update permissions on the file in the database
        Query query = entityManager.createQuery("UPDATE File f SET f.privateAccess=:private, f.readPermission=:read, f.writePermission=:write WHERE f.name=:name");
        query.setParameter( "private", file.hasPrivateAccess() );
        query.setParameter( "read", file.hasReadPermission() );
        query.setParameter( "write", file.hasWritePermission() );
        query.setParameter( "name", file.getName() );
        query.executeUpdate();
        // commit the transaction via helper
        commitTransaction();
    }

    /**
     * Remove a file from the db
     * @param file the file we want to remove
     */
    public void removeFile (File file) {
        // use the helper to prep the entitymanager for acting on the dbs
        EntityManager entityManager = beginTransaction();
        // delete the file entry from the db
        Query query = entityManager.createQuery("DELETE FROM File f WHERE f.name=:name");
        query.setParameter("name", file.getName());
        query.executeUpdate();
        // finally commit the action
        commitTransaction();
    }

    /**
     * Helper for starting transactions
     * @return a set up entitymanager
     */
    private EntityManager beginTransaction() {
        // create an entitymanager via the helper
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        // set it to be threadlocal
        managerThreadLocal.set(entityManager);
        // get the transaction object
        EntityTransaction transaction = entityManager.getTransaction();
        // if the transaction isnt already ongoing, then start
        if ( !transaction.isActive() ) {
            transaction.begin();
        }
        return entityManager;
    }

    /**
     * Helper for commiting transactions
     */
    private void commitTransaction() {
        // Commit the transaction
        managerThreadLocal.get().getTransaction().commit();
    }
}
