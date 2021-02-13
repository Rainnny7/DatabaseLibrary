package me.braydon.database.impl.redis;

import me.braydon.database.DatabaseRepository;

/**
 * Implementation of the {@link DatabaseRepository} for the {@link RedisDatabase}
 *
 * @author Braydon
 */
public class RedisRepository extends DatabaseRepository<RedisDatabase> {
    public RedisRepository(RedisDatabase database) {
        super(database);
    }
}