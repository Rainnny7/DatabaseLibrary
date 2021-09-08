package me.braydon.database.impl.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.braydon.database.IDatabase;
import me.braydon.database.IRepositoryDatabase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The implementation of {@link IDatabase} for Redis
 *
 * @author Braydon
 */
@Getter @Slf4j(topic = "RedisDatabase")
public class RedisDatabase implements IDatabase<RedisProperties>, IRepositoryDatabase<RedisRepository> {
    private static final Object LOCK = new Object();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    private RedisProperties properties;
    private final Set<RedisPool> pools = new HashSet<>();
    private MessagingService messagingService;

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
                redisPool.setJedisPool(new JedisPool(properties.getPoolConfig(), redisPool.getHost(), redisPool.getPort()));
            }
            messagingService = new MessagingService(this);
            if (properties.isDebugging())
                log.debug("Setup " + pools.size() + " pools in " + (System.currentTimeMillis() - started) + "ms");
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
     * Get a dummy connection of the repository for this database type
     *
     * @return the repository
     * @apiNote This will create a new instance of a repository each time, it's recommended to save a reference
     * of the repository for future use
     */
    @Override
    public RedisRepository getDummyRepository() {
        synchronized (LOCK) {
            return new RedisRepository(this);
        }
    }

    /**
     * Add a {@link RedisPool} to the database with the given name and {@link RedisPoolType}
     *
     * @param name the name of the pool
     * @param host the host of the pool
     * @param port the port of the pool
     * @param type the type of the pool
     */
    public RedisDatabase withPool(@NonNull String name, @NonNull String host, int port, RedisPoolType type) {
        synchronized (LOCK) {
            return withPool(name, host, port, null, type);
        }
    }

    /**
     * Add a {@link RedisPool} to the database with the given name and {@link RedisPoolType}
     *
     * @param name the name of the pool
     * @param host the host of the pool
     * @param port the port of the pool
     * @param auth the password for the pool
     * @param type the type of the pool
     */
    public RedisDatabase withPool(@NonNull String name, @NonNull String host, int port, String auth, RedisPoolType type) {
        synchronized (LOCK) {
            pools.add(new RedisPool(name, host, port, auth, type));
            return this;
        }
    }

    /**
     * Get a {@link RedisPool} by it's name
     *
     * @param name the name of the pool
     * @return the pool
     */
    public RedisPool getPool(String name) {
        synchronized (LOCK) {
            for (RedisPool redisPool : pools) {
                if (redisPool.getName().equals(name)) {
                    return redisPool;
                }
            }
            return null;
        }
    }

    /**
     * Get a {@link RedisPool} that matches the given writable param.
     *
     * @param writable whether or not to get a writable pool
     * @return the pool
     */
    public RedisPool getPool(boolean writable) {
        synchronized (LOCK) {
            return getPool(writable ? RedisPoolType.MASTER : RedisPoolType.SLAVE);
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
            // Fetch all of the RedisPool's with the same type
            List<RedisPool> pools = new ArrayList<>();
            for (RedisPool redisPool : this.pools) {
                if (redisPool.getType() != type)
                    continue;
                pools.add(redisPool);
            }
            if (pools.isEmpty()) {
                // If there are no available SLAVE pools, try and fetch a MASTER pool
                if (type == RedisPoolType.SLAVE) {
                    if (properties.isDebugging())
                        log.debug("Cannot find an available pool type of " + type.name() + ", attempting to find a MASTER pool");
                    return getPool(RedisPoolType.MASTER);
                }
                throw new IllegalStateException("Cannot find an available pool for the type " + type.name());
            }
            return pools.get(ThreadLocalRandom.current().nextInt(pools.size()));
        }
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
            messagingService = null;
        }
    }

    @AllArgsConstructor @Getter
    public static class MessagingService {
        private static int MESSENGER_ID, DISPATCH_ID;

        private final RedisDatabase redisDatabase;
        private final Map<RedisMessenger, JedisPubSub> messengers = new HashMap<>();

        /**
         * Add a {@link RedisMessenger}
         *
         * @param messenger the messenger to add
         */
        public void addMessenger(RedisMessenger messenger) {
            new Thread(() -> {
                try (Jedis jedis = redisDatabase.getPool(RedisPoolType.SLAVE).getResource()) {
                    JedisPubSub jedisPubSub = new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            messenger.onMessage(channel, message);
                        }

                        @Override
                        public void onPMessage(String pattern, String channel, String message) {
                            if (messenger.usingPatterns())
                                messenger.onPatternMessage(pattern, channel, message);
                        }
                    };
                    synchronized (LOCK) {
                        messengers.put(messenger, jedisPubSub);
                    }
                    if (messenger.usingPatterns())
                        jedis.psubscribe(jedisPubSub, messenger.getChannels());
                    else jedis.subscribe(jedisPubSub, messenger.getChannels());
                }
            }, "Redis Messenger - " + ++MESSENGER_ID).start();
        }

        /**
         * Dispatch a Redis command to the given channel with the given message
         *
         * @param channel the channel
         * @param message the message
         */
        public void dispatch(String channel, String message) {
            EXECUTOR_SERVICE.execute(() -> {
                try (Jedis jedis = redisDatabase.getPool(RedisPoolType.SLAVE).getResource()) {
                    jedis.publish(channel, message);
                }
            });
        }
    }
}