package me.braydon.database.impl.redis;

import me.braydon.database.DatabaseRepository;

/**
 * @author Braydon
 */
public class RedisRepository extends DatabaseRepository<RedisDatabase> {
    public RedisRepository(RedisDatabase database) {
        super(database);
    }

    /**
     * Get a {@link RedisPool} by it's name
     *
     * @param name the name of the pool
     * @return the pool
     */
    public RedisPool getPool(String name) {
        return database.getPool(name);
    }

    /**
     * Get a {@link RedisPool} that matches the given writable param.
     *
     * @param writable whether or not to get a writable pool
     * @return the pool
     */
    public RedisPool getPool(boolean writable) {
        return getPool(writable);
    }

    /**
     * Get a {@link RedisPool} with the given {@link RedisPoolType}
     *
     * @param type the type of the pool
     * @return the pool
     */
    public RedisPool getPool(RedisPoolType type) {
        return database.getPool(type);
    }
}