package server.integration;

import server.model.User;

import javax.persistence.*;

public class UserDAO {

    // entitymanagerfactory for the class
    private final EntityManagerFactory entityManagerFactory;
    // To provide thread local entitymanagers we create an object for doing so
    private final ThreadLocal<EntityManager> managerThreadLocal = new ThreadLocal<>();

    /**
     * userDAO constructor, create the entitymanagerfactory for this persistence unit
     */
    public UserDAO() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory( "org.hibernate.netprog.jpa" );
    }

    /**
     * Find a user by searching for their username
     * @param username  the user we want to find
     * @return          the corresponding user object grabbed from the db
     */
    public User findUser (String username) {
        /// create a new entitymanager
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // create a new query, select the user that matches the provided username
        Query query = entityManager.createQuery( "SELECT u FROM User u WHERE u.username=:username" );
        // set the username parameter of the query to be the provided username
        query.setParameter( "username", username );
        // create a user object to hold the user from the db
        User user;

        // if we find a matching user in the db we can return it
        // if there is no user returned then nothing matched so we return null
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }

        // when done we close the entitymanager and return the user object
        entityManager.close();
        return user;
    }

    /**
     * Put a user into the db
     * @param user  the user we want to put into the db
     */
    public void storeUser (User user) {
        // prep entitymanager
        EntityManager entityManager = beginTransaction();
        // make the user object managed and persistent
        entityManager.persist( user );
        // commit the transaction
        commitTransaction();
    }

    /**
     * Remove a user from the db
     * @param user  the user to be removed
     */
    public void removeUser (User user) {
        // prep entitymanager via helper
        EntityManager entityManager = beginTransaction();
        // merge the file to find it, then remove it
        entityManager.remove( entityManager.merge( user ) );
        // finally commit the transaction
        commitTransaction();
    }

    /**
     * Helper for starting transaction
     * @return a set up entity manager
     */
    private EntityManager beginTransaction() {
        // create an entitymanager via the helper
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        // set it to be threadlocal
        managerThreadLocal.set( entityManager );
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
        managerThreadLocal.get().getTransaction().commit();
    }
}
