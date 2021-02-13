package me.braydon.database.impl.redis;

/**
 * @author Braydon
 */
public interface RedisMessenger {
    default void onMessage(String channel, String message) {}

    default void onPatternMessage(String pattern, String channel, String message) {}

    default boolean usingPatterns() {
        return false;
    }

    String[] getChannels();
}