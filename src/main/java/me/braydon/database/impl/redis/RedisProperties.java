package me.braydon.database.impl.redis;

import lombok.Getter;
import lombok.NonNull;
import me.braydon.database.properties.AuthenticationProperties;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis properties for a database hold information such as the host, port, password, database and pool config
 *
 * @author Braydon
 */
@Getter
public class RedisProperties extends AuthenticationProperties {
    public static final int DEFAULT_PORT = 6379;

    private final int database;
    private final JedisPoolConfig poolConfig;

    public RedisProperties(int database) {
        super("", -1, null, null);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxWaitMillis(1000L);
        poolConfig.setMaxTotal(30);
        poolConfig.setMaxIdle(100);
        poolConfig.setMinIdle(5);
        poolConfig.setBlockWhenExhausted(true);
        this.database = database;
        this.poolConfig = poolConfig;
    }

    public RedisProperties(int database, @NonNull JedisPoolConfig poolConfig) {
        super("", -1, null, null);
        this.database = database;
        this.poolConfig = poolConfig;
    }

    @Override
    public RedisProperties withDebugging() {
        debugging = true;
        return this;
    }
}