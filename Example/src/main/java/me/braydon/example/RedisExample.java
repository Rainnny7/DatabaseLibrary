package me.braydon.example;

import me.braydon.database.impl.redis.RedisDatabase;
import me.braydon.database.impl.redis.RedisMessenger;
import me.braydon.database.impl.redis.RedisPoolType;
import me.braydon.database.impl.redis.RedisProperties;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Braydon
 */
public class RedisExample {
    public static void main(String[] args) throws InterruptedException {
        // Setting up the Redis database with the "test" pool using the provided host, port, and password, and
        // connecting to the database at index 1. For the sake of testing, we have the debugging mode enabled
        // using the #withDebugging method
        RedisDatabase database = (RedisDatabase) new RedisDatabase()
                .withPool("test", "127.0.0.1", RedisProperties.DEFAULT_PORT, RedisPoolType.MASTER) // Adding a MASTER pool with the name "test"
                .connect(new RedisProperties(1).withDebugging());

        // Adding a messenger to the Redis database with the channel "example"
        database.getMessagingService().addMessenger(new RedisMessenger() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("Received \"" + message + "\" on channel \"" + channel + "\"");
            }

            @Override
            public String[] getChannels() {
                return new String[] { "example" };
            }
        });

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

        // Sending the message "hello" to the channel "example"
        Thread.sleep(2000L);
        database.getMessagingService().dispatch("example", "hello");
    }
}