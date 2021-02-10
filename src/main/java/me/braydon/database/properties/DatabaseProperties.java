package me.braydon.database.properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Properties for a database hold information such as the host and port
 *
 * @author Braydon
 */
@RequiredArgsConstructor @Getter
public class DatabaseProperties {
    @NonNull private final String host;
    private final int port;
    protected boolean debugging;

    public DatabaseProperties withDebugging() {
        debugging = true;
        return this;
    }
}