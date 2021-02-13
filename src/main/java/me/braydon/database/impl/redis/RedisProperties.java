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
    private final int database;
    private final JedisPoolConfig poolConfig;

    public RedisProperties(@NonNull String host, int port, String password, int database) {
        super(host, port, null, password);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxWaitMillis(1000L);
        poolConfig.setMaxTotal(30);
        poolConfig.setMaxIdle(100);
        poolConfig.setMinIdle(5);
        poolConfig.setBlockWhenExhausted(true);
        this.database = database;
        this.poolConfig = poolConfig;
    }

    public RedisProperties(@NonNull String host, int port, String password, int database, @NonNull JedisPoolConfig poolConfig) {
        super(host, port, null, password);
        this.database = database;
        this.poolConfig = poolConfig;
    }

    @Override
    public RedisProperties withDebugging() {
        debugging = true;
        return this;
    }
}