package me.braydon.database.impl.redis;

import lombok.NonNull;
import me.braydon.database.IDatabase;
import me.braydon.database.properties.AuthenticationProperties;

/**
 * @author Braydon
 */
public class RedisDatabase implements IDatabase<AuthenticationProperties, RedisRepository> {
    /**
     * Connect to the database server with the given properties
     *
     * @param properties the properties to connect with
     * @param onConnect  the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    @Override
    public IDatabase<AuthenticationProperties, RedisRepository> connect(@NonNull AuthenticationProperties properties, Runnable onConnect) {
        throw new UnsupportedOperationException();
    }

    /**
     * Connect to the database server with the given properties and uri
     *
     * @param properties the properties to connect with
     * @param uri        the uri to use to make a connection to the database server
     * @param onConnect  the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    @Override
    public IDatabase<AuthenticationProperties, RedisRepository> connect(@NonNull AuthenticationProperties properties, @NonNull String uri, Runnable onConnect) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a dummy connection of the repository for this database type
     *
     * @return the repository
     * @apiNote This will create a new instance of a repository each time, it's recommended to save a reference
     * of the repository for future use
     */
    @Override
    public RedisRepository getDummyRepository() {
        return new RedisRepository(this);
    }
}