package me.braydon.database;

import lombok.NonNull;
import me.braydon.database.properties.DatabaseProperties;

/**
 * This class represents a database type
 *
 * @author Braydon
 * @param <P> the properties type
 * @param <R> the repository type
 */
public interface IDatabase<P extends DatabaseProperties, R> {
    /**
     * Connect to the database server with the given properties
     * @param properties the properties to connect with
     * @return the database instance
     */
    default IDatabase<P, R> connect(@NonNull P properties) {
        return connect(properties, (Runnable) null);
    }

    /**
     * Connect to the database server with the given properties
     * @param properties the properties to connect with
     * @param onConnect the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    IDatabase<P, R> connect(@NonNull P properties, Runnable onConnect);

    /**
     * Connect to the database server with the given properties and uri
     * @param properties the properties to connect with
     * @param uri the uri to use to make a connection to the database server
     * @return the database instance
     */
    default IDatabase<P, R> connect(@NonNull P properties, @NonNull String uri) {
        return connect(properties, uri, null);
    }

    /**
     * Connect to the database server with the given properties and uri
     * @param properties the properties to connect with
     * @param uri the uri to use to make a connection to the database server
     * @param onConnect the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    IDatabase<P, R> connect(@NonNull P properties, @NonNull String uri, Runnable onConnect);

    /**
     * Get a dummy connection of the repository for this database type
     * @return the repository
     * @apiNote This will create a new instance of a repository each time, it's recommended to save a reference
     *          of the repository for future use
     */
    R getDummyRepository();

    /**
     * Cleanup the database and close connections
     */
    void cleanup();
}