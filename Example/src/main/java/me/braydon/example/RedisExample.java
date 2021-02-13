package me.braydon.example;

import me.braydon.database.impl.redis.RedisDatabase;
import me.braydon.database.impl.redis.RedisPoolType;
import me.braydon.database.impl.redis.RedisProperties;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Braydon
 */
public class RedisExample {
    public static void main(String[] args) {
        // Connecting to Redis using the provided host, port, password, and database index.
        // For the sake of testing, we have the debugging mode enabled using the #withDebugging method
        RedisDatabase database = (RedisDatabase) new RedisDatabase()
                .withPool("test", RedisPoolType.MASTER) // Adding a MASTER pool with the name "test"
                .connect(new RedisProperties("127.0.0.1", 6379, null, 1).withDebugging());

        // Getting a master Redis pool and setting the key "test" to a map with the key "test" and "hi" as the value
        try (Jedis jedis = database.getPool(RedisPoolType.MASTER).getResource()) {
            jedis.hmset("test", new HashMap<>() {{
                put("test", "hi");
            }});
        }

        // Getting a master Redis pool and getting the key "test" that we set above and printing it to the terminal
        try (Jedis jedis = database.getPool(RedisPoolType.MASTER).getResource()) {
            for (Map.Entry<String, String> entry : jedis.hgetAll("test").entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }
    }
}