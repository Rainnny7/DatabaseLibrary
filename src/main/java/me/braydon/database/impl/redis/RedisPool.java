package me.braydon.database.impl.redis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

/**
 * @author Braydon
 */
@RequiredArgsConstructor @Getter
public class RedisPool {
    private final String name;
    private final RedisPoolType type;
    @Setter(AccessLevel.PROTECTED) private RedisDatabase database;
    @Setter(AccessLevel.PROTECTED) private JedisPool jedisPool;

    /**
     * Get a resource ({@link Jedis}) from the {@link JedisPool}
     *
     * @return the resource
     */
    public Jedis getResource() {
        if (jedisPool == null || (jedisPool.isClosed()))
            throw new NullPointerException("Cannot get a resource from the pool");
        Jedis jedis = jedisPool.getResource();
        if (database.getProperties().getPassword() != null)
            jedis.auth(database.getProperties().getPassword());
        jedis.select(database.getProperties().getDatabase());
        return jedis;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        RedisPool redisPool = (RedisPool) other;
        return Objects.equals(name, redisPool.name) && type == redisPool.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}