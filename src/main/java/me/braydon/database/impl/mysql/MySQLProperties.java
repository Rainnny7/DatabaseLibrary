package me.braydon.database.impl.mysql;

import lombok.Getter;
import lombok.NonNull;
import me.braydon.database.properties.AuthenticationProperties;

/**
 * MySQL properties for a database hold information such as the host, port, username, password, and database
 *
 * @author Braydon
 */
@Getter
public class MySQLProperties extends AuthenticationProperties {
    public static final int DEFAULT_PORT = 3306;

    private final String database;

    public MySQLProperties(@NonNull String host, int port, String username, @NonNull String password, @NonNull String database) {
        super(host, port, username, password);
        this.database = database;
    }

    @Override
    public MySQLProperties withDebugging() {
        debugging = true;
        return this;
    }
}