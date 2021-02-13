package me.braydon.database;

import lombok.NonNull;
import me.braydon.database.properties.DatabaseProperties;

/**
 * This class represents a database type
 *
 * @author Braydon
 * @param <E> the properties type
 * @param <R> the repository type
 */
public interface IDatabase<E extends DatabaseProperties> {
    /**
     * Connect to the database server with the given properties
     * @param properties the properties to connect with
     * @return the database instance
     */
    default IDatabase<E> connect(@NonNull E properties) {
        return connect(properties, (Runnable) null);
    }

    /**
     * Connect to the database server with the given properties
     * @param properties the properties to connect with
     * @param onConnect the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    IDatabase<E> connect(@NonNull E properties, Runnable onConnect);

    /**
     * Connect to the database server with the given properties and uri
     * @param properties the properties to connect with
     * @param uri the uri to use to make a connection to the database server
     * @return the database instance
     */
    default IDatabase<E> connect(@NonNull E properties, @NonNull String uri) {
        return connect(properties, uri, null);
    }

    /**
     * Connect to the database server with the given properties and uri
     * @param properties the properties to connect with
     * @param uri the uri to use to make a connection to the database server
     * @param onConnect the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    IDatabase<E> connect(@NonNull E properties, @NonNull String uri, Runnable onConnect);

    /**
     * Cleanup the database and close connections
     */
    void cleanup();
}