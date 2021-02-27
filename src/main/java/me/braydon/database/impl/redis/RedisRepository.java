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
     * Get a writable {@link RedisPool}
     *
     * @return the pool
     */
    public RedisPool getPool(boolean writable) {
        return database.getPool(RedisPoolType.MASTER);
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