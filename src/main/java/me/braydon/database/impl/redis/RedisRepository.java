package me.braydon.database.impl.redis;

import me.braydon.database.DatabaseRepository;

/**
 * @author Braydon
 */
public class RedisRepository extends DatabaseRepository<RedisDatabase> {
    public RedisRepository(RedisDatabase database) {
        super(database);
    }
}