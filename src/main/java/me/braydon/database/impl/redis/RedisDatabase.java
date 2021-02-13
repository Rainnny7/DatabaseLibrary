package me.braydon.database.impl.redis;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.braydon.database.IDatabase;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The implementation of {@link IDatabase} for Redis
 *
 * @author Braydon
 */
@Getter @Slf4j(topic = "RedisDatabase")
public class RedisDatabase implements IDatabase<RedisProperties> {
    private static final Object LOCK = new Object();

    private RedisProperties properties;
    private final Set<RedisPool> pools = new HashSet<>();

    /**
     * Connect to the database server with the given properties
     *
     * @param properties the properties to connect with
     * @param onConnect  the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    @Override
    public IDatabase<RedisProperties> connect(@NonNull RedisProperties properties, Runnable onConnect) {
        synchronized (LOCK) {
            this.properties = properties;
            if (pools.isEmpty())
                throw new IllegalStateException("Found no pools to setup");
            long started = System.currentTimeMillis();
            for (RedisPool redisPool : pools) {
                redisPool.setDatabase(this);
                redisPool.setJedisPool(new JedisPool(properties.getPoolConfig(), properties.getHost(), properties.getPort()));
            }
            if (properties.isDebugging())
                log.info("Setup " + pools.size() + " pools in " + (System.currentTimeMillis() - started) + "ms");
            if (onConnect != null)
                onConnect.run();
        }
        return this;
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
    public IDatabase<RedisProperties> connect(@NonNull RedisProperties properties, @NonNull String uri, Runnable onConnect) {
        throw new UnsupportedOperationException();
    }

    /**
     * Cleanup the database and close connections
     */
    @Override
    public void cleanup() {
        synchronized (LOCK) {
            properties = null;
            for (RedisPool redisPool : pools) {
                JedisPool jedisPool = redisPool.getJedisPool();
                if (jedisPool == null || (jedisPool.isClosed()))
                    continue;
                jedisPool.close();
            }
            pools.clear();
        }
    }

    /**
     * Add a {@link RedisPool} to the database with the given name and {@link RedisPoolType}
     *
     * @param name the name of the pool
     * @param type the type of the pool
     */
    public RedisDatabase withPool(String name, RedisPoolType type) {
        synchronized (LOCK) {
            pools.add(new RedisPool(name, type));
            return this;
        }
    }

    /**
     * Get a {@link RedisPool} with the given {@link RedisPoolType}
     *
     * @param type the type of the pool
     * @return the pool
     */
    public RedisPool getPool(RedisPoolType type) {
        synchronized (LOCK) {
            List<RedisPool> pools = new ArrayList<>();
            for (RedisPool redisPool : this.pools) {
                if (redisPool.getType() != type)
                    continue;
                pools.add(redisPool);
            }
            if (pools.isEmpty())
                throw new IllegalStateException("Cannot find an available pool for the type " + type.name());
            return pools.get(ThreadLocalRandom.current().nextInt(pools.size()));
        }
    }
}